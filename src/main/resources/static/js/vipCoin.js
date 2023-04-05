let vipCoinApp = new Vue({
    el: '#vipCoin',
    data: {
        hwnds: [],
        vipCoinConfig: new VipCoinConfig(),
        vipCoinConfigNameList: [],
        currentVipCoinConfigName: "",
    },
    created() {
        this.getConfig();
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
        getConfig() {
            let url = "vipCoin/getConfig";
            axios.get(url).then((res) => {
                this.vipCoinConfig = new VipCoinConfig(res.data.result);
                this.vipCoinConfigNameList = [];
                for (let i = 0; i < this.vipCoinConfig.vipCoinOneConfigList.length; i++) {
                    this.vipCoinConfigNameList.push(this.vipCoinConfig.vipCoinOneConfigList[i].name);
                }
                this.currentVipCoinConfigName = this.vipCoinConfigNameList[0];
            });
        },
        addNewVipCoinConfig() {
            let s = prompt("输入新建配置的名称", `新配置${this.vipCoinConfigNameList.length + 1}`);
            if (!s) {
                return;
            }
            for (let vipCoinConfigNameListKey in this.vipCoinConfigNameList) {
                if (this.vipCoinConfigNameList[vipCoinConfigNameListKey] === s) {
                    alert("配置名称重复！");
                    return;
                }
            }

            console.log("1111");
            return;

            let url = "vipCoin/addNewVipCoinConfig";
            axios.get(url).then((res) => {
                this.vipCoinConfig = new VipCoinConfig(res.data.result);
                this.vipCoinConfigNameList = [];
                for (let i = 0; i < this.vipCoinConfig.vipCoinOneConfigList.length; i++) {
                    this.vipCoinConfigNameList.push(this.vipCoinConfig.vipCoinOneConfigList[i].name);
                }
                this.currentVipCoinConfigName = this.vipCoinConfigNameList[0];
            });
        },
        deleteThisConfig(name) {
            if (!confirm(`确定要删除配置【${name}】吗？`)) {
                return;
            }
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