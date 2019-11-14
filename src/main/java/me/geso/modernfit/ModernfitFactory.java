package me.geso.modernfit;

import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Proxy;

public class ModernfitFactory {
    @SuppressWarnings("unchecked")
    public static <T> T build(WebClient webClient, Class<T> klass) {
        return (T) Proxy.newProxyInstance(ModernfitFactory.class.getClassLoader(), new Class[] {klass}, new ModernfitHandler(webClient));
    }
}
