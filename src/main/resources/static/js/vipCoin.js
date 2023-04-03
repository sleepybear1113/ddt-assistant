let vipCoinApp = new Vue({
    el: '#vipCoin',
    data: {
        hwnds: [],
    },
    created() {
    },
    methods: {
        getDdtHwnds() {
            this.hwnds = [];
            let url = "dm/getDdtHwnds";
            axios.get(url).then((res) => {
                let list = res.data.result;
                for (let i = 0; i < list.length; i++) {
                    this.hwnds.push(list[i]);
                }
            });
        },
        startVipCoinLoop(hwnd) {
            let url = "vipCoin/start";
            axios.get(url, {"params": {hwnd}}).then((res) => {
            });
        },
        stopAll() {
            let url = "vipCoin/stopAll";
            axios.get(url).then((res) => {
            });
        },
        stop(hwnd) {
            let url = "vipCoin/stop";
            axios.get(url, {"params": {hwnd}}).then((res) => {
            });
        },
        suspend(hwnd) {
            let url = "vipCoin/suspend";
            axios.get(url, {"params": {hwnd}}).then((res) => {
            });
        },
        resume(hwnd) {
            let url = "vipCoin/resume";
            axios.get(url, {"params": {hwnd}}).then((res) => {
            });
        },
    }
});