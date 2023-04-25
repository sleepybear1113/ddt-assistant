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
        getConfig(newName) {
            let url = "vipCoin/getConfig";
            axios.get(url).then((res) => {
                this.vipCoinConfig = new VipCoinConfig(res.data.result);
                this.vipCoinConfigNameList = [];
                for (let i = 0; i < this.vipCoinConfig.vipCoinOneConfigList.length; i++) {
                    this.vipCoinConfigNameList.push(this.vipCoinConfig.vipCoinOneConfigList[i].name);
                }
                this.currentVipCoinConfigName = this.vipCoinConfigNameList[0];
                if (newName != null) {
                    this.vipCoinConfigNameList.forEach(value => {
                        if (newName === value) {
                            this.currentVipCoinConfigName = newName;
                        }
                    });
                }
            });
        },
        updateOneConfig(configName) {
            let config = null;
            for (let i = 0; i < this.vipCoinConfig.vipCoinOneConfigList.length; i++) {
                let oneConfig = this.vipCoinConfig.vipCoinOneConfigList[i];
                if (oneConfig.name === configName) {
                    config = oneConfig;
                }
            }
            if (config == null) {
                alert("null");
                return;
            }

            let url = "vipCoin/updateOneConfig";
            axios.post(url, config).then(() => {
                this.getConfig(config.newName);
            });
        },
        addNewVipCoinConfig() {
            let newName = prompt("输入新建配置的名称", `新配置${this.vipCoinConfigNameList.length + 1}`);
            if (!newName) {
                return;
            }
            for (let vipCoinConfigNameListKey in this.vipCoinConfigNameList) {
                if (this.vipCoinConfigNameList[vipCoinConfigNameListKey] === newName) {
                    alert("配置名称重复！");
                    return;
                }
            }

            let url = "vipCoin/addNewVipCoinConfig";
            axios.get(url, {"params": {newName}}).then((res) => {
                this.getConfig();
                console.log("配置新增完成")
            });
        },
        deleteThisConfig(name) {
            if (!confirm(`确定要删除配置【${name}】吗？`)) {
                return;
            }

            let url = "vipCoin/deleteThisConfig";
            axios.get(url, {"params": {name}}).then((res) => {
                this.getConfig();
            });
        },
        addCondition(conditionList) {
            conditionList.push(new VipCoinCondition().defaultCondition());
        },
        addSingleCondition(conditionList) {
            conditionList.push(new SingleCondition().defaultCondition());
        },
        deleteCondition(conditionList, index) {
            conditionList.splice(index, 1);
            if (this.vipCoinConfig.vipCoinOneConfigList == null || this.vipCoinConfig.vipCoinOneConfigList.length <= 0) {
                return;
            }

            for (let i = 0; i < this.vipCoinConfig.vipCoinOneConfigList.length; i++) {
                let config = this.vipCoinConfig.vipCoinOneConfigList[i];
                if (!config.vipCoinConditionList) {
                    continue;
                }
                for (let j = 0; j < config.vipCoinConditionList.length; j++) {
                    let vipCoinConditionListElement = config.vipCoinConditionList[j];
                    console.log(vipCoinConditionListElement)
                    if (vipCoinConditionListElement == null || vipCoinConditionListElement.singleConditionList.length === 0) {
                        config.vipCoinConditionList.splice(j, 1);
                        break;
                    }
                }
            }
        },
        addStopCondition(vipCoinOneConfig) {
            vipCoinOneConfig.vipCoinStopConditionList.push(new VipCoinStopCondition().defaultConfig());
        },
        deleteStopCondition(vipCoinOneConfig, index) {
            vipCoinOneConfig.vipCoinStopConditionList.splice(index, 1);
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