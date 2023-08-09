package it.vitalegi.structurizr.gen;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.JavacTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerAnalysisApp {

    Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        var path = Path.of("src/main/java/it/vitalegi/cosucce/resource/BoardResource.java");
        ContainerAnalysisApp app = new ContainerAnalysisApp();
        app.process(path);
    }

    public void process(Path file) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null,
                StandardCharsets.UTF_8)) {
            final Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file.toFile()));
            final JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, null, null, null,
                    compilationUnits);
            final Iterable<? extends CompilationUnitTree> compilationUnitTrees = javacTask.parse();
            final ClassTree classTree = (ClassTree) compilationUnitTrees.iterator().next().getTypeDecls().get(0);

            log.info("Class name={}", classTree.getSimpleName());
            classTree.getModifiers().getAnnotations().stream().map(this::toStringAnnotation).forEach(a -> {
                log.info("> " + a);
            });

            final List<? extends Tree> classMemberList = classTree.getMembers();
            var methods = classMemberList.stream().filter(MethodTree.class::isInstance).map(MethodTree.class::cast)
                                         .collect(Collectors.toList());

            // just prints the names of the methods
            methods.stream().forEachOrdered(m -> {
                log.info("method: {}", m.getName(), m.getModifiers());
                m.getModifiers().getAnnotations().stream().map(this::toStringAnnotation).forEach(a -> {
                    log.info("> " + a);
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String toStringAnnotation(AnnotationTree at) {
        return at.getAnnotationType() + "(" + at.getArguments().stream().map(this::toStringAnnotationArgument)
                                                .collect(Collectors.joining(", ")) + ")";
    }

    protected String toStringAnnotationArgument(ExpressionTree et) {
        // primitive argument
        if (et instanceof LiteralTree) {
            var e = (LiteralTree) et;
            var value = e.getValue();
            if (value != null) {
                return value.toString();
            }
            return null;
        }
        if (et instanceof MemberSelectTree) {
            var e = (MemberSelectTree) et;
            var value = e.getIdentifier();
            if (value != null) {
                return value.toString();
            }
            return null;
        }
        if (et instanceof AssignmentTree) {
            var e = (AssignmentTree) et;
            return e.getVariable().toString() + "=" + e.getExpression().toString();
        }

        var accepted = Arrays.asList(AnnotatedTypeTree.class, AnnotationTree.class, ArrayAccessTree.class,
                                     AssignmentTree.class, BinaryTree.class, CompoundAssignmentTree.class,
                                     ConditionalExpressionTree.class, ErroneousTree.class, IdentifierTree.class,
                                     InstanceOfTree.class, LambdaExpressionTree.class, LiteralTree.class,
                                     MemberReferenceTree.class, MemberSelectTree.class, MethodInvocationTree.class,
                                     NewArrayTree.class, NewClassTree.class, ParenthesizedTree.class,
                                     TypeCastTree.class, UnaryTree.class)
                             .stream().filter(c -> c.isInstance(et)).map(Class::getSimpleName)
                             .collect(Collectors.joining(", "));
        log.debug("Cannot process argument, possible classes: {}", accepted);
        return null;
    }

    protected String toStringAnnotations(ModifiersTree mt) {
        return mt.getAnnotations().stream().map(this::toStringAnnotation).collect(Collectors.joining(", "));

    }

}
