package com.seungmoo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// SOURCE : SOURCE레벨에서 읽어서 컴파일하고 AnnotationProcessor로 돌린 내용은 바이트코드에 반영
// Annotation Processor로 소스를 돌리는 경우, SOURCE까지만 해주면 된다.
// CLASS : 위의 내용 + Annotation 또한 바이트코드에서 까지 유지
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE) // Interface, Class, Enum 이렇게 사용 가능.
public @interface Magic {
}
