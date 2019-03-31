package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.aws.codestar.projecttemplates.GatewayResponse;
import com.aws.codestar.projecttemplates.constant.AppConstant;
import com.aws.codestar.projecttemplates.db.ProductDao;
import com.aws.codestar.projecttemplates.enums.ReturnMessageEnum;
import com.aws.codestar.projecttemplates.vo.RequestVo;
import com.aws.codestar.projecttemplates.vo.ResponseVo;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class HelloWorldHandler implements RequestHandler<RequestVo, ResponseVo> {

    @Override
    public ResponseVo handleRequest(final RequestVo request, final Context context) {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        return new GatewayResponse(new JSONObject().put("Output", "Hello World!").toString(), headers, 200);
//

        LambdaLogger logger = context.getLogger();
        logger.log( "SearchWord: '"+request.getSearchWord()+"'\taddFlag:"+request.isAddFlag()+"\t");
        return doWork( request.getSearchWord(), request.isAddFlag());

    }

    private ResponseVo doWork(String searchWord, boolean addFlag){


        if(searchWord == null || searchWord.length()==0 ){
            return ResponseVo.fail( ReturnMessageEnum.PARAM_NULL.getCode(), ReturnMessageEnum.PARAM_NULL.getName() );
        }else if(searchWord.length() > AppConstant.MAX_LENGTH_PARAM_TITLE){
            return ResponseVo.fail( ReturnMessageEnum.PARAM_ERROR.getCode(),
                    "参数[searchWord]超过最大长度"+ AppConstant.MAX_LENGTH_PARAM_TITLE );
        }

        ProductDao productDao = new ProductDao();
        try {
            if(addFlag){
                //新加商品
                productDao.save( searchWord );

                return ResponseVo.success();
            }else {
                //查询商品列表

                return ResponseVo.success(productDao.getList( searchWord, AppConstant.MAX_COUNT_PRODUCT_SIZE ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseVo.fail( ReturnMessageEnum.SYSTEM_EXCEPTION.getCode(), ReturnMessageEnum.SYSTEM_EXCEPTION.getName() );
        }

    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        HelloWorldHandler application = new HelloWorldHandler();
        System.out.println(application.doWork( "鞋子", false ));

    }
}
