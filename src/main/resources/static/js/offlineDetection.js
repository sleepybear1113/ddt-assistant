let offlineDetectionApp = new Vue({
    el: '#offlineDetection',
    data: {
        tabName: "游戏异常检测",
        offlineDetection: {
            delay: "",
            emailRemind: false,
            disconnect: true,
            tokenExpired: false,
            offsite: false,
            leaveGame: false,
            whiteScreen: false,
        }

    },
    created() {
        this.get();
    },
    methods: {
        update: function () {
            let url = "offlineDetection/update";
            axios.post(url, this.offlineDetection).then(() => {
                showInfo("成功");
            });
        },
        get: function () {
            let url = "offlineDetection/get";
            axios.get(url).then((res) => {
                this.offlineDetection = new OfflineDetection(res.data.result);
            });
        },
    }
});