无服务器架构 自动化构建 演示项目
==============================================

这是一个AWS 无服务器架构的Demo项目，帮助开发者用Lambda 和API Gateway
快速的自动化构建项目。


项目架构文档请参考[AWS Serverless服务架构 Demo ](https://github.com/dikers/serverless)
------------------

这两个项目架构是一样的， 不同的地方一个是手工进行Lambda 和 Amazon API
Gateway的部署和配置， 另外一个则完全用自动化的方式进行配置，
建议第一次接触的时候，可以先手动配置理解基本概念，等熟悉以后再学习用template
自动化进行配置。 

文件目录
-----------



* README.md - 说明文件
* buildspec.yml - AWS CodeBuild 用这个文件自动构建服务。 
* pom.xml - Maven 配置文件
* src/main - Java 相关代码
* src/test - 测试代码
* template.yml - AWS CloudFormation用这个文件来来部署AWS Lambda 和Amazon
  API Gateway。 
* web 项目用到的静态资源
* db 项目用到的数据库脚本
* template-configuration.json 用来配置资源文件



操作步骤
===============================================



第一步  从CodeStar上选择模板文件 
-------------------------------------------------- 
这里选择 java 实现的Lambda 模板文件， 基础的代码CodeStar 用生成好。
选择好要上传的代码仓库， CodeStar就会开始自动编译和部署，
几分钟以后就可以访问应用了。
![image](https://github.com/dikers/serverless/blob/master/doc/picture/50.jpg?raw=true)


第二步 准备 Lambda 和静态网页的代码
-----------------------------------------------

本项目的功能比较简单，请参考
* [Lambda 代码](https://github.com/dikers/codestar/tree/master/src)
     
* [Web 代码](https://github.com/dikers/codestar/tree/master/web)




第三步   修改用来部署的Template文件
-----------------------------------------------

下面是对Template的的一些说明 详细教程请参考:
[https://docs.aws.amazon.com/zh_cn/AWSCloudFormation/latest/UserGuide/Welcome.html](https://docs.aws.amazon.com/zh_cn/AWSCloudFormation/latest/UserGuide/Welcome.html)


```

AWSTemplateFormatVersion: 2010-09-09    #模板文件的唯一合法版本号 
Transform:
- AWS::Serverless-2016-10-31
- AWS::CodeStar


# 定义参数
Parameters:
  ProjectId:   # 参数名称， 项目的ID
    Type: String   # 参数的名称， String表示一个字符串
    Description: AWS CodeStar projectID used to associate new resources to team members
  CodeDeployRole:
    Type: String
    Description: IAM role to allow AWS CodeDeploy to manage deployment of AWS Lambda functions
  Stage:           # 项目的部署环境 用来区分正式环境还是开发阶段。 
    Type: String
    Description: The name for a project pipeline stage, such as Staging or Prod, for which resources are provisioned and deployed.
    Default: ''

Globals:
  Function:
    AutoPublishAlias: live  #自动检查代码是否更新， 如果更新就自动发布服务。
    DeploymentPreference:
      Enabled: true
      Type: Canary10Percent5Minutes  #有新版本发布的时候， 先发布10%的流量到新版本，如果没有问题， 5分钟之后全部切换到新版本， 如果失败回滚到上一个版本。 
      Role: !Ref CodeDeployRole      #代码部署的角色


# 定义资源
Resources:

  PostHelloWorld:   #参数名称
    Type: AWS::Serverless::Function       #参数类型，表示一个AWS无服务架构的函数。 
    Properties:
      Handler: com.aws.codestar.projecttemplates.handler.HelloWorldHandler    # 要处理的java 对象
      Runtime: java8             #运行环境
      Role:
        Fn::GetAtt: # Fn::GetAtt 内部函数返回模板中的资源的属性值,  这里是指获取LambdaExecutionRole的ARN名称
        - LambdaExecutionRole
        - Arn
      Events:         # 该函数所要处理的事件， 处理根路径的 post 请求。
        PostEvent:       
          Type: Api
          Properties:
            Path: /
            Method: post
  LambdaExecutionRole:      #定义一个Role ， 用来控制安全访问
    Description: Creating service role in IAM for AWS Lambda
    Type: AWS::IAM::Role
    Properties:
      RoleName: !Sub 'CodeStar-${ProjectId}-Execution${Stage}'       #
      AssumeRolePolicyDocument:   # 复制和粘贴策略
        Statement:          #用来授权的策略
        - Effect: Allow    # 允许执行操作
          Principal: 
            Service: [lambda.amazonaws.com]    # 访问Lambda服务
          Action: sts:AssumeRole    # 操作堆栈（Stack）的权限
      Path: /
      ManagedPolicyArns:   #可以访问AWS上的资源路径
        -  arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
        
      #用于为角色设置权限边界的策略的 ARN
      # !Sub  是 Fn::Sub简写， 作用是将输入字符串中的变量替换为指定的值。  
      # ${AWS::Partition} aws 可用区Id
      # ${AWS::AccountId} 账户Id
      # ${ProjectId} 项目Id
      PermissionsBoundary: !Sub 'arn:${AWS::Partition}:iam::${AWS::AccountId}:policy/CodeStar_${ProjectId}_PermissionsBoundary'


```




第四步  修改CodeBuild 用来自动构建服务的脚本文件
------------------------------------------------- 

详细教程请参考:
[https://docs.aws.amazon.com/zh_cn/codebuild/latest/userguide/getting-started.html#getting-started-input-bucket](https://docs.aws.amazon.com/zh_cn/codebuild/latest/userguide/getting-started.html#getting-started-input-bucket)

```
version: 0.2

phases:
  install:
    commands:
      # 安装最新的 aws命令行控制工具  awscli
      - pip install --upgrade awscli
  pre_build:
    commands:
      - echo Test started on `date`   #打印时间
      - mvn clean compile test        #执行java java 代码的编译， 并做单元测试。 
  build:
    commands:
      - echo Build started on `date`    #打印时间
      - mvn package shade:shade         #打包java ， 生成jar 文件
      - mv target/HelloWorld-1.0.jar .  # copy jar 到当前目录
      - unzip HelloWorld-1.0.jar        # 解压 jar
      - rm -rf target tst src buildspec.yml pom.xml HelloWorld-1.0.jar  #删除临时文件
      # 
      - aws cloudformation package --template template.yml --s3-bucket $S3_BUCKET --output-template template-export.yml
  post_build:
    commands:
      # AWS CodeStar 需要下面的脚本， 替换字符串
      # template-configuration.json 文件中的 AWS Partition, AWS Region, account ID and project ID 需要被替换。  
      - sed -i.bak 's/\$PARTITION\$/'${PARTITION}'/g;s/\$AWS_REGION\$/'${AWS_REGION}'/g;s/\$ACCOUNT_ID\$/'${ACCOUNT_ID}'/g;s/\$PROJECT_ID\$/'${PROJECT_ID}'/g' template-configuration.json
artifacts:
      # 两个生成好的文件， 用于构建环境。 
  files:
    - template-export.yml
    - template-configuration.json


```






第五步 修改代码，并提交，CodeStar自动编译和部署
------------------------------------------------------------------------- 
下面是CodeStar 自动化编译和部署的截图

![image](https://github.com/dikers/serverless/blob/master/doc/picture/40.jpg?raw=true)


 
AWS CodeBuild学习资料： 
https://docs.aws.amazon.com/codebuild/latest/userguide/concepts.html

AWS Serverless Application Model (AWS SAM) 学习资料： 
https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md

AWS Lambda 学习资料： 
http://docs.aws.amazon.com/lambda/latest/dg/deploying-lambda-apps.html

Codestar 学习资料：
http://docs.aws.amazon.com/codestar/latest/userguide/welcome.html

 