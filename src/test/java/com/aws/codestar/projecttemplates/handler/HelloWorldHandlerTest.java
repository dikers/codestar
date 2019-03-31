package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;

import com.aws.codestar.projecttemplates.GatewayResponse;

import com.aws.codestar.projecttemplates.constant.AppConstant;
import com.aws.codestar.projecttemplates.db.DbHelper;
import com.aws.codestar.projecttemplates.enums.ReturnMessageEnum;
import com.aws.codestar.projecttemplates.vo.RequestVo;
import com.aws.codestar.projecttemplates.vo.ResponseVo;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you build your project.
 */
@DisplayName("Tests for HelloWorldHandler")
public class HelloWorldHandlerTest {

    private static final String EXPECTED_CONTENT_TYPE = "application/json";
    private static final String EXPECTED_RESPONSE_VALUE = "Hello World!";
    private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;

    // A mock class for com.amazonaws.services.lambda.runtime.Context
    private final MockLambdaContext mockLambdaContext = new MockLambdaContext();
    private final Object input = new Object();


    /**
     * Initializing variables before we run the tests.
     * Use @BeforeAll for initializing static variables at the start of the test class execution.
     * Use @BeforeEach for initializing variables before each test is run.
     */
    @BeforeAll
    static void setup() {
        // Use as needed.
    }

    /**
     * De-initializing variables after we run the tests.
     * Use @AfterAll for de-initializing static variables at the end of the test class execution.
     * Use @AfterEach for de-initializing variables at the end of each test.
     */
    @AfterAll
    static void tearDown() {
        // Use as needed.
    }

    /**
     * Basic test to verify the result obtained when calling {@link HelloWorldHandler} successfully.
     */
    @Test
    @DisplayName("Basic test for request handler")
    void testHandleRequest() {

        RequestVo requestVo = new RequestVo();
        requestVo.setAddFlag( false );
        requestVo.setSearchWord( "鞋" );

        ResponseVo response = (ResponseVo) new HelloWorldHandler().handleRequest(requestVo, mockLambdaContext);

        // Verify the response obtained matches the values we expect.
        assertEquals(ReturnMessageEnum.SUCCESS.getName(), response.getMessage());
        assertEquals( ReturnMessageEnum.SUCCESS.getCode(), response.getStatus().intValue());
    }
}
