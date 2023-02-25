let serverLog = new Vue({
    el: '#server-log-vue',
    data: {
        mode: true,
        serverLog: "服务器日志：\n",
        shown: false,
        autoReconnect: true,
        serverLogAll: "",
        logFiles: [],
        n: 200,
        open: true,
    },
    created() {
        this.initWebSocket();
    },

    methods: {
        initWebSocket() {
            if (typeof (WebSocket) == "undefined") {
                this.addServerLog("您的浏览器不支持实时获取日志");
                return;
            }
            let url;
            if (window.location.protocol === "https:") {
                url = "wss:";
            } else {
                url = "ws:";
            }
            url += "//" + window.location.host + "/serverLogSocket";
            let socket = new WebSocket(url); //打开事件
            socket.onopen = () => {
                this.addServerLog("==> 已连接到服务器进行日志获取");
            };
            //获得消息事件
            socket.onmessage = (msg) => {
                this.addServerLog(msg.data);
                //发现消息进入 调后台获取
            };
            //关闭事件
            socket.onclose = () => {
                this.addServerLog("==> 服务器通信已关闭");

                setTimeout(() => {
                    this.addServerLog("==> 重连中...");
                    this.initWebSocket();
                }, 2000)

            };
            //发生了错误事件
            socket.onerror = () => {
                this.addServerLog("==> 服务器通信发生错误");
            };
        },
        addServerLog(msg) {
            this.addServerLogAll("\n" + msg);

            const scroll = document.getElementById("server-log");
            let goBottom = scroll.scrollTop >= (scroll.scrollHeight - scroll.offsetHeight - 30);
            const newLine = document.createElement("div");
            newLine.className = "log-line";
            newLine.innerHTML = msg.replaceAll("\n", "<br/>");
            scroll.appendChild(newLine);
            if (goBottom) {
                scroll.scrollTop = scroll.scrollHeight;
            }
        },
        listServerLogs() {
            let url = "system/getLocalAllFiles";
            let params = {
                params: {
                    "path": "日志文件/",
                    "excludePath": true,
                }
            };
            axios.get(url, params).then((res) => {
                this.logFiles = res.data.result;
            });
        },
        fetchLog(path) {
            let url = "system/getLastSomeRows";
            let params = {
                params: {
                    "filename": "日志文件/" + path,
                    n: this.n,
                }
            };
            axios.get(url, params).then((res) => {
                this.serverLogAll = "";
                this.addServerLogAll(res.data.result);
            });
        },
        addServerLogAll(msg) {
            const scroll = document.getElementById("server-log-textarea");
            let goBottom = scroll.scrollTop >= (scroll.scrollHeight - scroll.offsetHeight - 50);
            this.serverLogAll += msg;
            if (goBottom) {
                scroll.scrollTop = scroll.scrollHeight;
            }
        },
        scrollToBottom() {
            let scroll = document.getElementById("server-log-textarea");
            scroll.scrollTop = scroll.scrollHeight;
        },
        scrollToTop() {
            let scroll = document.getElementById("server-log-textarea");
            scroll.scrollTop = 0;
        },
    }
});