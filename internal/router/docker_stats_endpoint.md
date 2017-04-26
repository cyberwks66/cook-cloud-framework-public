# Docker

```


user www-data docker;

worker_processes auto;
pid /run/nginx.pid;

events {
        worker_connections 768;
        # multi_accept on;
}

http {
        upstream dockerapi {
                server unix:/var/run/docker.sock;

        }

        server {
                listen 127.0.0.1:9999;
                location / {
                        proxy_pass http://dockerapi;

                        limit_except GET {
                                deny all;
                        }
                }
        }
}

```