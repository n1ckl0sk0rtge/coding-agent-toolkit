package dev.nickl0s.opencode;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Path;

final class OpenCodeReference {
    private OpenCodeReference() {
    }

    static String format(Project project, VirtualFile file, int startLine, int endLine) {
        String projectPath = project.getBasePath();
        String path = projectPath == null
                ? file.getPath()
                : relativePath(projectPath, file.getPath());
        return format(path, startLine, endLine);
    }

    static String format(String path, int startLine, int endLine) {
        return "@" + path.replace('\\', '/') + "#L" + startLine + "-" + endLine;
    }

    private static String relativePath(String projectPath, String filePath) {
        Path project = Path.of(projectPath).toAbsolutePath().normalize();
        Path file = Path.of(filePath).toAbsolutePath().normalize();
        return file.startsWith(project) ? project.relativize(file).toString() : file.toString();
    }
}

