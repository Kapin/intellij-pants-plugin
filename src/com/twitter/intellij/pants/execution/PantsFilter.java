package com.twitter.intellij.pants.execution;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.print.DocFlavor;

/**
 * Created by ajohnson on 6/18/14.
 */

public class PantsFilter implements Filter {
  private final Project project;

  public PantsFilter(Project project) {
    this.project = project;
  }

  @Nullable
  @Override
  public Result applyFilter(final String text, int entireLength) {
    PantsFilterInfo info = parseLine(text);
    if (info == null) {return null;}
    int start = entireLength - text.length() + info.getStart();
    int end = entireLength - text.length() + info.getEnd();
    return new Result(start, end, new OpenFileHyperlinkInfo(project, info.getFile(), info.getLineNumber()));
  }

  @Nullable
  public static PantsFilterInfo parseLine(@NotNull String line) {
    int i = 0;
    while (i < line.length() && Character.isSpaceChar(line.charAt(i))) {
      ++i;
    }
    final int start = i;
    while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\n' && line.charAt(i) != ':') {
      ++i;
    }
    int end = i;
    i++;
    String filePath = line.substring(start, end);
    while (i < line.length() && Character.isDigit(line.charAt(i))) {
      ++i;
    }
    int lineNumber = 1;
    try {
      lineNumber = Integer.parseInt(line.substring(end + 1, i)) - 1;
      end = i;
    } catch (Exception e) {

    }
    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
    if (file == null) {
      return null;
    }
    return new PantsFilterInfo(start, end, file, lineNumber);
  }

  public static class PantsFilterInfo {

    private final int start;
    private final int end;
    private final int lineNumber;
    private final VirtualFile file;
    public PantsFilterInfo(int start, int end, VirtualFile file, int lineNumber) {
      this.start = start;
      this.end = end;
      this.file = file;
      this.lineNumber = lineNumber;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public int getLineNumber() {
      return lineNumber;
    }

    public VirtualFile getFile() {
      return file;
    }
  }
}
