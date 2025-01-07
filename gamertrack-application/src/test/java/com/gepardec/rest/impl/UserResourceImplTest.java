package com.gepardec.rest.impl;

import com.gepardec.RestConfig;
import com.gepardec.core.services.UserService;
import com.gepardec.model.User;
import com.gepardec.rest.api.UserResource;
import com.gepardec.rest.model.command.CreateUserCommand;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ArquillianExtension.class)
public class UserResourceImplTest {

        @ArquillianResource
        private URL deploymentURL;

        @Inject
        UserResourceImpl userResource;

        @Inject
        UserService userService;

        @Deployment(testable = false)
        public static WebArchive create()
        {
            return ShrinkWrap.create(WebArchive.class)
                    .addPackage(User.class.getPackage())
                    .addClasses(UserResource.class, UserResourceImpl.class, RestConfig.class);
        }

        @Test
        void createUser(@ArquillianResteasyResource UserResource userResource)
        {

            //        Given
            CreateUserCommand createUserCommand = new CreateUserCommand("John", "Doe");

            //        When
            final User result = userResource.createUser(createUserCommand).readEntity(User.class);

            //        Then
            assertNotNull(result);
            assertNotNull(result.getId());
            assertNotNull(result.getToken());
            assertEquals(createUserCommand.firstname(), result.getFirstname());
            assertEquals(createUserCommand.lastname(), result.getLastname());
        }

}

