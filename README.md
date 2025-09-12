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


