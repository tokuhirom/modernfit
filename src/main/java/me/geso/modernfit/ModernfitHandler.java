package me.geso.modernfit;

import me.geso.modernfit.annotation.GET;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ModernfitHandler implements InvocationHandler {
    private WebClient webClient;

    ModernfitHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpMethodHolder httpMethod = getHttpMethod(method);
        WebClient.RequestBodyUriSpec requestHeadersUriSpec = webClient.method(httpMethod.getHttpMethod());
        requestHeadersUriSpec.uri(httpMethod.getPath());
        WebClient.ResponseSpec responseSpec = requestHeadersUriSpec.retrieve();
        return processResponse(method, method.getGenericReturnType(), responseSpec);
        // TODO @Path
        // TODO @Header
        // TODO @Body
    }

    private Object processResponse(Method method, Type returnType, WebClient.ResponseSpec responseSpec) {
        if (returnType instanceof ParameterizedType) {
            // e.g. Mono<T>
            String typeName = ((ParameterizedType) returnType).getRawType().getTypeName();
            if (typeName.equals("reactor.core.publisher.Mono")) {
                Type typeArgument = (((ParameterizedType) returnType).getActualTypeArguments())[0];
                if (typeArgument instanceof Class) {
                    // e.g. Mono<String>
                    return responseSpec.bodyToMono(ParameterizedTypeReference.forType(typeArgument));
                }
            }
        } else if (returnType instanceof Class) {
            // e.g. String
            return responseSpec.bodyToMono(ParameterizedTypeReference.forType(returnType))
                    .block();
        }
        throw new IllegalArgumentException("Unsupported return value type '" + returnType.getTypeName() + "' for "
                + method.getName() + "(" + returnType.getClass().getName() + ")");
    }

    private HttpMethodHolder getHttpMethod(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            Class<? extends Annotation> httpMethod = annotation.annotationType();
            if (httpMethod == GET.class) {
                return new HttpMethodHolder(HttpMethod.GET, ((GET) annotation).value());
                // TODO support other methods
            } else {
                throw new IllegalStateException("Should not reach here");
            }
        }
        throw new IllegalStateException(method.getDeclaringClass().getName() + "#" + method.getName()
                + " should have http method annotation");
    }
}
