package com.seungmoo;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
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

        for (Element element : elements) {
            Name elementName = element.getSimpleName();
            // kind : element가 어느 타입의 Element 인지(Class 인지, Interface 인지 등등)
            if (element.getKind() != ElementKind.INTERFACE) {
                // Kind.ERROR로 printMessage 던지면 오류를 뱉어준다.
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Magic annotation can not be used on " + elementName);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + elementName);
            }

            // javapoet을 이용한 소스코드 생성하기
            // anno-moja의 Moja interface type instance의 method code를 생성 해보자.
            TypeElement typeElement = (TypeElement) element;
            ClassName className = ClassName.get(typeElement);

            // anno-moja의 Moja의 pullOut 메서드의 코드를 생성
            MethodSpec pullOut = MethodSpec.methodBuilder("pullOut")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String.class)
                    .addStatement("return $S", "Rabbit") // 문자열 Rabbit 을 return 한다.
                    .build();

            // MogicMoja라는 class를 만든다.
            TypeSpec MagicMoja = TypeSpec.classBuilder("MagicMoja")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(pullOut)
                    .addSuperinterface(className) // @Magic annotation이 붙어 있는 INTERFACE를 구현하는 클래스이다.
                    .build();
            // 여기까지는 그냥 메모리 상에 객체를 정의 한 것임, 실제 bytecode에 반영시켜야 한다. (Filer 인터페이스)

            // Filer 인터페이스
            //•	소스 코드, 클래스 코드 및 리소스를 생성할 수 있는 인터페이스 (핵심적인 클래스)
            Filer filer = processingEnv.getFiler();
            // javapoet을 통해 쉽게 생성할 수 있다.
            // MagicMoja라는 File을 지정한 패키지안에 만들어라.
            try {
                JavaFile.builder(className.packageName(), MagicMoja)
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + e);
            }
        }

        // true return : annotation을 처리 한 것임. 다른 Processor들에게 더이상 이 애노테이션을 처리하도록 넘기지 않음.
        // @Magic Annotation에 특화된 AnnotationProcessor를 만들기 때문에 true로 리턴한다.
        return true;
    }
}
