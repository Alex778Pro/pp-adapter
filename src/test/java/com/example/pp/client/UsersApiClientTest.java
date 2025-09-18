package com.example.pp.client;

import com.example.pp.entity.ClientInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsersApiClientTest {
    @Test
    void testHaveFeignClientAnnotation() {
        FeignClient feignClientAnnotation = UsersApiClient.class.getAnnotation(FeignClient.class);

        assertNotNull(feignClientAnnotation, "Интерфейс должен иметь аннотацию @FeignClient");
        assertEquals("usersApiClient", feignClientAnnotation.name());
        assertEquals("${spring.api.url}", feignClientAnnotation.url());
    }

    @Test
    void testGetClientsMethod_HavePostMappingAnnotation() throws NoSuchMethodException {
        Method method = UsersApiClient.class.getMethod("getClients");
        PostMapping postMapping = method.getAnnotation(PostMapping.class);

        assertNotNull(postMapping, "Метод getClients должен иметь аннотацию @PostMapping");
        assertArrayEquals(new String[]{"/api/v1/getClient"}, postMapping.value());
    }
    @Test
    void testGetClientsMethod_ReturnTypeListOfClientInfo() throws NoSuchMethodException {
        Method method = UsersApiClient.class.getMethod("getClients");
        Class<?> returnType = method.getReturnType();

        assertEquals(List.class, returnType);
    }
    @Test
    void testGetClientByIdMethod_HavePostMappingAnnotation() throws NoSuchMethodException {
        Method method = UsersApiClient.class.getMethod("getClientById", String.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);

        assertNotNull(postMapping, "Метод getClientById должен иметь аннотацию @PostMapping");
        assertArrayEquals(new String[]{"/api/v1/getClient/{id}"}, postMapping.value());
    }
    @Test
    void testGetClientByIdMethod_HavePathVariableAnnotation() throws NoSuchMethodException {
        Method method = UsersApiClient.class.getMethod("getClientById", String.class);
        Parameter parameter = method.getParameters()[0];
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);

        assertNotNull(pathVariable, "Метод getClientById должен иметь аннотацию @PathVariable");
        assertEquals("id", pathVariable.value());
    }

    @Test
    void testGetClientByIdMethod_ReturnType() throws NoSuchMethodException {
        Method method = UsersApiClient.class.getMethod("getClientById", String.class);
        Class<?> returnType = method.getReturnType();

        assertEquals(ClientInfo.class, returnType);
    }

    @Test
    void testInterface_Public(){
        int modifiers = UsersApiClient.class.getModifiers();

        assertTrue(java.lang.reflect.Modifier.isPublic(modifiers),"Интерфейс UsersApiClient должен быть public ");
    }

    @Test
    void testMethods_Public() throws NoSuchMethodException {
        Method getClientsMethod = UsersApiClient.class.getMethod("getClients");
        Method getClientByIdMethod = UsersApiClient.class.getMethod("getClientById", String.class);

        assertTrue(
                java.lang.reflect.Modifier.isPublic(getClientsMethod.getModifiers()),
                "Метод getClient должен быть public"
                );
        assertTrue(
                java.lang.reflect.Modifier.isPublic(getClientByIdMethod.getModifiers()),
                "Метод getClientById должен быть public"
                );
    }
}