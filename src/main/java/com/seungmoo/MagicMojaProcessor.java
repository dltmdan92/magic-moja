package com.seungmoo;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

// MagicMojaProcessor를 Processor로 등록해라.
// javax.annotation.processing.Processor --> 이거를 MagicMojaProcessor를 컴파일할 때 자동으로 만들어준다.
// 우리가 수동으로 수행했던 것과 똑같다.
@AutoService(Processor.class)
public class MagicMojaProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // Magic이라는 Annotation을 처리하겠다.
        return Set.of(Magic.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // 소스 코드 몇버전을 지원하겠는가.
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Magic.class);

        for (Element e : elements) {
            Name elementName = e.getSimpleName();
            // kind : element가 어느 타입의 Element 인지(Class 인지, Interface 인지 등등)
            if (e.getKind() != ElementKind.INTERFACE) {
                // Kind.ERROR로 printMessage 던지면 오류를 뱉어준다.
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Magic annotation can not be used on " + elementName);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + elementName);
            }
        }

        // true return : annotation을 처리 한 것임. 다른 Processor들에게 더이상 이 애노테이션을 처리하도록 넘기지 않음.
        // @Magic Annotation에 특화된 AnnotationProcessor를 만들기 때문에 true로 리턴한다.
        return true;
    }
}
