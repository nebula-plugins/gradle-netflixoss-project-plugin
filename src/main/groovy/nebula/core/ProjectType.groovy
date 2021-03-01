package nebula.core

import groovy.transform.Canonical
import org.gradle.api.Project

@Canonical
class ProjectType {
    boolean isRootProject
    boolean isParentProject
    boolean isLeafProject

    ProjectType(Project project) {
        isRootProject = (project == project.rootProject)
        isParentProject = project.rootProject.subprojects.any { it.parent == project } // Parent of any projects, aka Uncle/Aunt project
        isLeafProject = !isParentProject
    }
}
