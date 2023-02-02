let firstApp = new Vue({
    el: '#first',
    data: {
        ipList: [[], [], [], []],
        showAvailableIpAddr: false,
        ipInfoList: ["【内网】ipv4地址", "【公网】ipv4地址", "【内网】ipv6地址", "【公网】ipv6地址"],
    },
    created() {
        this.getAvailableIpAddr();
    },
    methods: {
        initApp: function () {
        },
        getVersion: function () {
            return indexApp.appVersion;
        },
        getAvailableIpAddr: function () {
            let url = "system/getAvailableIpAddr";
            axios.get(url).then((res) => {
                let lists = res.data.result;
                if (lists == null || lists.length === 0) {
                    this.ipList = [[], [], [], []];
                    return;
                }

                this.ipList = lists;
            });
        },
    }
});