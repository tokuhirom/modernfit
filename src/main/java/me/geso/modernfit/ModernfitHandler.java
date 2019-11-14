package me.geso.modernfit;

import me.geso.modernfit.annotation.DELETE;
import me.geso.modernfit.annotation.GET;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModernfitHandler implements InvocationHandler {
    private WebClient webClient;

    ModernfitHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = processHttpMethod(method);
        WebClient.ResponseSpec responseSpec = requestHeadersUriSpec.retrieve();
        return processResponse(method, method.getGenericReturnType(), responseSpec);
        // TODO @Path
    }

    private Object processResponse(Method method, Type returnType, WebClient.ResponseSpec responseSpec) {
        if (returnType instanceof ParameterizedType) {
            // Mono<T>
            String typeName = ((ParameterizedType) returnType).getRawType().getTypeName();
            if (typeName.equals("reactor.core.publisher.Mono")) {
                Type typeArgument = (((ParameterizedType) returnType).getActualTypeArguments())[0];
                if (typeArgument instanceof Class) {
                    // e.g. Mono<String>
                    return responseSpec.bodyToMono(ParameterizedTypeReference.forType(typeArgument));
                }
            }
        } else if (returnType instanceof Class) {
            return responseSpec.bodyToMono(ParameterizedTypeReference.forType(returnType))
                    .block();
        }
        throw new IllegalArgumentException("Unsupported return value type '" + returnType.getTypeName() + "' for "
                + method.getName() + "(" + returnType.getClass().getName() + ")");
    }

    private WebClient.RequestHeadersUriSpec<?> processHttpMethod(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            Class<? extends Annotation> httpMethod = annotation.annotationType();
            if (httpMethod == GET.class) {
                return webClient.get();
            } else {
                throw new IllegalStateException("Should not reach here");
            }
        }
        throw new IllegalStateException(method.getDeclaringClass().getName() + "#" + method.getName()
                + " should have http method annotation");
    }
}
