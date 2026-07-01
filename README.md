# springcloud
注册中心为nacos
首先需要启动nacos服务，控制台界面用户名密码nacos，nacos

启动多个网关服务，使用nginx负载均衡

并行多个 service_hi服务，与service_hello服务
网关服务实现自动路由

# enabled：默认为false，设置为true表明spring cloud gateway开启服务发现和路由的功能，网关自动根据注册中心的服务名为每个服务创建一个router，将以服务名开头的请求路径转发到对应的服务
spring.cloud.gateway.discovery.locator.enabled = true
# lowerCaseServiceId：启动 locator.enabled=true 自动路由时，路由的路径默认会使用大写ID，若想要使用小写ID，可将lowerCaseServiceId设置为true
spring.cloud.gateway.discovery.locator.lower-case-service-id = true

通过网关动态路由分别调用两个服务
如
hello服务
http://localhost:8760/service_gateway/service-hello/hello?name=zhangsan
多次点击，轮询到各个节点
hi服务
http://localhost:8760/service_gateway/service-hi/hi?name=zhangsan
多次点击，轮询到各个节点

访问service-hello服务再通过feign调用service-hi服务
http://localhost:8758/service_gateway/service-hello/user/zhagnsan?token=1213

同时支持配置热更新
添加bootstrap.yaml，配置外部配置文件，本例在nacos注册中心配置管理中添加配置
data-id命名规则为
${spring.application.name}-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
本例实际为 service-hi-dev.yaml
作为文件id，来读取配置。


nginx 配置内容

worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    #gzip  on;
	
	#定义一个服务 location中可以引用
	upstream myapp{
	server localhost:8758 weight=1;
	server localhost:8759 weight=1;
	server localhost:8760 weight=1;
	}

    server {
        listen       80;
        server_name  localhost;
		# 转发websocket需要的设置
        proxy_set_header X-Real_IP $remote_addr;
        proxy_set_header Host $host;
        proxy_set_header X_Forward_For $proxy_add_x_forwarded_for;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';

		#bop地址访问本地应用
        location / {
			add_header Access-Control-Allow-Origin *;
			add_header Access-Control-Allow-Headers X-Requested-With;
			add_header Access-Control-Allow-Methods GET,POST,PUT,DELETE,OPTIONS;
			proxy_pass http://myapp/service_gateway/;
        }
		
}
}

加入认证：

##服务器获取token
GET http://localhost:8860/oauth/token?client_secret=abcdef&grant_type=password&username=wanghr&password=123456&client_id=cloud_client

###验证token
GET http://localhost:8860/oauth/check_token?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic2VydmljZS1oZWxsbyIsInNlcnZpY2UtaGkiXSwiZXhwIjoxNzYwMTUyMjM3LCJ1c2VyX25hbWUiOiJ3YW5naHIiLCJqdGkiOiJiYjNmMzM0ZS02MTJmLTRmOGEtYWNjMC1hMzdiY2U0NjBjM2IiLCJjbGllbnRfaWQiOiJjbG91ZF9jbGllbnQiLCJzY29wZSI6WyJhbGwiXX0.1KTJ-_61nmOc5SX35NI9-_yh-tmRTFlyVIX238UnxwY

###刷新token
GET http://localhost:8860/oauth/token?grant_type=refresh_token&client_secret=abcdef&client_id=cloud_client&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic2VydmljZS1oZWxsbyIsInNlcnZpY2UtaGkiXSwidXNlcl9uYW1lIjoid2FuZ2hyIiwic2NvcGUiOlsiYWxsIl0sImF0aSI6ImFhOWUwMTFlLTgwNGMtNGY4Mi1hN2I5LTM5MjEwYTVkMmUwZiIsImV4cCI6MTc2MDIzODc5OSwianRpIjoiNTVkMWUyNzYtNGIzZi00OWJhLThkZGMtYWFmYmMzN2FmYmY5IiwiY2xpZW50X2lkIjoiY2xvdWRfY2xpZW50In0.pgbPfVIbsPf1n8nGPlmN9Iw8q7mu5C202IWoX32_guQ

##通过网关调用hello服务
GET http://localhost:8758/service_gateway/service-hello/hello?name=zhangsan
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic2VydmljZS1oZWxsbyIsInNlcnZpY2UtaGkiXSwiZXhwIjoxNzYwNjA4OTkzLCJ1c2VyX25hbWUiOiJ3YW5naHIiLCJqdGkiOiIzN2Q3NGU2Ny1mM2FjLTQyM2UtOGMzZC1hYzZhMTllZWYyZWIiLCJjbGllbnRfaWQiOiJjbG91ZF9jbGllbnQiLCJzY29wZSI6WyJhbGwiXX0.Lg1SDJWoriDmWD8ZSP9ZjI70KPLzrMlSy244DV5LZjI
###
##通过网关调用hello服务，hello feign调用hi服务,服务之间使用 拦截器传递 Authorization 请求头
GET http://localhost:8758/service_gateway/service-hello/user/zhangsan
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic2VydmljZS1oZWxsbyIsInNlcnZpY2UtaGkiXSwiZXhwIjoxNzYwNjA4OTkzLCJ1c2VyX25hbWUiOiJ3YW5naHIiLCJqdGkiOiIzN2Q3NGU2Ny1mM2FjLTQyM2UtOGMzZC1hYzZhMTllZWYyZWIiLCJjbGllbnRfaWQiOiJjbG91ZF9jbGllbnQiLCJzY29wZSI6WyJhbGwiXX0.Lg1SDJWoriDmWD8ZSP9ZjI70KPLzrMlSy244DV5LZjI
###

##通过网关获取token
GET http://localhost:8758/service_gateway/service-auth/oauth/token?client_secret=abcdef&grant_type=password&username=wanghr&password=123456&client_id=cloud_client

##网关做了nginx负载均衡后的访问路劲，此时我开启了三个网关服务.端口号分别为8758，8759，5760。
GET http://localhost/service-auth/oauth/token?client_secret=abcdef&grant_type=password&username=wanghr&password=123456&client_id=cloud_client

##nginx->gateway->auth服务检测token合法性
GET http://localhost/service-auth/oauth/check_token?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic2VydmljZS1oZWxsbyIsInNlcnZpY2UtaGkiXSwidXNlcl9uYW1lIjoid2FuZ2hyIiwic2NvcGUiOlsiYWxsIl0sInJvbGVzIjpbXSwib3JnYW5pemF0aW9uIjoi5p-Q57uE57uHIiwiZXhwIjoxNzgyMjkyODYzLCJ1c2VySWQiOiLnlKjmiLdJRCIsImp0aSI6ImUzZjFiNDJjLTk1ZTctNDE1Ny04MGIwLWRlNjRjYThmYThhMSIsImNsaWVudF9pZCI6ImNsb3VkX2NsaWVudCJ9.uwanAYDpuNUpQe2QZrQJaKj5gceawBuIJ4uvLr7_ad4
###
