无服务器架构 自动化构建 演示项目
==============================================

这是一个AWS 无服务器架构的一个Demo项目，帮助开发者用Lambda 和API Gateway
快速的自动化构建项目。


项目架构文档请参考[AWS Serverless服务架构 Demo ](https://github.com/dikers/serverless)
------------------

这两个项目架构是一样的， 不同的地方， 一个是手工进行Lambda 和 Amazon API
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




 
AWS CodeBuild学习资料： 
https://docs.aws.amazon.com/codebuild/latest/userguide/concepts.html

AWS Serverless Application Model (AWS SAM) 学习资料： 
https://github.com/awslabs/serverless-application-model/blob/master/HOWTO.md

AWS Lambda 学习资料： 
http://docs.aws.amazon.com/lambda/latest/dg/deploying-lambda-apps.html

Codestar 学习资料：
http://docs.aws.amazon.com/codestar/latest/userguide/welcome.html

 