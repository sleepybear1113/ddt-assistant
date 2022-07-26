let auctionApp = new Vue({
    el: '#auction',
    data: {
        newFilterCondition: "",
        tabName: "拍卖场功能",
        auctionData: {
            auctionItemList: [],
            autoAddUnknown: true,
            confirm: false,
            sellType: "1",
        },
        hwnds: [],
        filterConditionButtonList: [],
        filterConditionSet: new Set(),
        selectedTab: "使用界面",
        tabs: [
            {name: "使用界面"},
            {name: "使用说明"},
        ],
    },
    created() {
        this.getItems();
        this.getFilterConditionList();
    },
    methods: {
        changeTab: function (tab) {
            this.selectedTab = tab.name;
        },
        getFilterConditionList: function () {
            let url = "auction/getFilterCondition";
            axios.get(url).then((res) => {
                let list = res.data.result;
                this.filterConditionButtonList = [];
                for (let i = 0; i < list.length; i++) {
                    this.addFilterConditionList(list[i]);
                }
            });
        },
        addFilterConditionList: function (condition) {
            if (condition == null || condition === "") {
                showInfo("请输入筛选条件");
                return;
            }
            this.newFilterCondition = "";
            for (let i = 0; i < this.filterConditionButtonList.length; i++) {
                let button = this.filterConditionButtonList[i];
                if (button.condition === condition) {
                    showInfo("该筛选条件已经存在");
                    return;
                }
            }
            this.filterConditionButtonList.push(new AuctionFilterButton({condition: condition}));
        },
        deleteFilterConditionList: function () {
            let chosenNum = 0;
            let left = [];
            for (let i = 0; i < this.filterConditionButtonList.length; i++) {
                let button = this.filterConditionButtonList[i];
                if (button.chosen === true) {
                    chosenNum++;
                } else {
                    left.push(button);
                }
            }
            if (chosenNum === 0) {
                showInfo("未选择删除的选项");
                return;
            }
            if (!confirm("是否删除筛选条件，数量：" + chosenNum)) {
                return;
            }

            this.filterConditionButtonList = left;
            this.filterConditionSet.clear();
            this.filterAuction();
        },
        saveFilterConditionList: function () {
            let list = [];
            for (let i = 0; i < this.filterConditionButtonList.length; i++) {
                let button = this.filterConditionButtonList[i];
                list.push(button.condition);
            }
            let url = "auction/updateFilterCondition";
            axios.post(url, list).then(() => {
                showInfo("成功！")
            });
        },
        addNewItem: function () {
            let itemSample = {
                enabled: false,
                name: "",
                ocrName: "",
                argueUnitPrice: "",
                mouthfulUnitPrice: "",
                minNum: "",
                auctionTime: "48",
            };
            this.auctionData.auctionItemList .push(new AuctionItem(itemSample));
        },
        deleteItem: function (index) {
            this.auctionData.auctionItemList.splice(index, 1);
        },
        getItems: function () {
            let url = "auction/getList";
            axios.get(url).then((res) => {
                this.auctionData = new AuctionData(res.data.result);
            });
        },
        changeAuctionTime: function (item, time) {
            item.auctionTime = time;
        },
        changeAuctionFilterList: function (button) {
            if (button.chosen === true) {
                this.filterConditionSet.delete(button.condition);
                button.chosen = false;
            } else {
                this.filterConditionSet.add(button.condition);
                button.chosen = true;
            }
            this.filterAuction();
        },
        filterAuction: function () {
            let size = this.filterConditionSet.size;
            let chooseAll = false;
            if (size === 0) {
                chooseAll = true;
            }
            let list = this.auctionData.auctionItemList;
            for (let i = 0; i < list.length; i++) {
                let item = list[i];
                if (chooseAll) {
                    item.shown = true;
                    continue
                }

                let s = item.name + "@=@" + item.ocrName;
                let match = 0;
                let conditions = this.filterConditionSet.keys();
                this.filterConditionSet.forEach((index, condition) => {
                    if (this.auctionStringMatch(s, condition)) {
                        match++;
                    }
                });
                item.shown = match === size;
            }
        },
        moveIndex: function (index, flag) {
            if (index === 0 && flag < 0) {
                return;
            }
            if (index === this.auctionData.auctionItemList.length - 1 && flag > 0) {
                return;
            }
            let tmp = this.auctionData.auctionItemList[index];
            this.auctionData.auctionItemList[index] = this.auctionData.auctionItemList[index + flag];
            this.auctionData.auctionItemList[index + flag] = tmp;
            let tmpList = this.auctionData.auctionItemList;
            this.auctionData.auctionItemList = [];
            this.auctionData.auctionItemList = tmpList;
        },
        savaItems: function () {
            let url = "auction/update";
            axios.post(url, this.auctionData).then(() => {
                showInfo("成功！")
            });
        },
        sell: function (hwnd) {
            let url = "auction/bindAndSell";
            for (let i = 0; i < this.hwnds.length; i++) {
                let item = this.hwnds[i];
                if (item.hwnd === hwnd) {
                    item.enabled = true;
                    break;
                }
            }
            axios.get(url, {
                params: {
                    hwnd: hwnd,
                    confirm: this.auctionData.confirm
                }
            }).then((res) => {
                showInfo(res.data.result);
            });
        },
        sellAll: function () {
            if (this.hwnds.length === 0) {
                alert("未有有效句柄");
                return;
            }
            let list = [];
            for (let i = 0; i < this.hwnds.length; i++) {
                let item = this.hwnds[i];
                if (item.enabled) {
                    list.push(item.hwnd);
                }
            }
            if (list.length === 0) {
                alert("未选择任何句柄！");
                return;
            }
            let url = "auction/bindAndSellAll";
            axios.get(url, {
                params: {
                    "hwnds": list.map(t => t).join(','),
                    confirm: this.auctionData.confirm
                }
            }).then((res) => {
                showInfo(res.data.result.string);
            });
        },
        stop: function (hwnd) {
            let url = "auction/stop";
            axios.get(url, {params: {hwnd: hwnd}}).then((res) => {
                showInfo(res.data.result);
            });
        },
        stopAll: function () {
            let url = "auction/stopAll";
            axios.get(url).then((res) => {
                showInfo(res.data.result);
            });
        },
        getGameShot: function (hwnd) {
            let url = "dm/getGameScreenPath";
            axios.get(url, {params: {hwnd: hwnd}}).then((res) => {
                let src = res.data.result.string;
                showImg(src);
            });
        },
        getDdtHwnds: function () {
            this.hwnds = [];
            let url = "dm/getDdtHwnds";
            axios.get(url).then((res) => {
                let list = res.data.result;
                for (let i = 0; i < list.length; i++) {
                    let item = list[i];
                    this.hwnds.push({enabled: false, hwnd: item});
                }
            });
        },
        auctionStringMatch(s, match) {
            if (s == null) {
                return false;
            }
            if (match == null || match === "") {
                return true;
            }

            let split = match.replace("，", ",").split(",");
            for (let i = 0; i < split.length; i++) {
                let sub = split[i];
                let subSplit = sub.split("%");
                let size = subSplit.length;
                for (let j = 0; j < subSplit.length; j++) {
                    if (s.indexOf(subSplit[j]) > -1) {
                        size--;
                    }
                }
                if (size <= 0) {
                    return true;
                }
            }
            return false;
        },
    }
});