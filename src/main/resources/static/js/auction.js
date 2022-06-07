let auctionApp = new Vue({
    el: '#auction',
    data: {
        auctionData: {
            auctionItemList: [],
            autoAddUnknown: true,
        },
        hwnds: [],
    },
    created() {
        this.getItems();
    },
    methods: {
        addNewItem: function () {
            let itemSample = {
                enabled: true,
                name: "",
                ocrName: "",
                argueUnitPrice: 1.0,
                mouthfulUnitPrice: 1.0,
                minNum: 1,
                auctionTime: "48",
            };
            this.auctionData.auctionItemList.push(itemSample);
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
        moveIndex: function (index, flag) {
            if (index === 0 && flag < 0) {
                return;
            }
            if (index === this.auctionData.auctionItemList.length - 1 && flag > 0) {
                return;
            }
            console.log(index, flag);
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
                alert("ok");
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
            axios.get(url, {params: {hwnd: hwnd}}).then((res) => {
                alert(res.data.result);
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
            axios.get(url, {params: {"hwnds": list.map(t => t).join(',')}}).then((res) => {
                showInfo(res.data.result.string);
            });
        },
        stop: function (hwnd) {
            let url = "auction/stop";
            axios.get(url, {params: {hwnd: hwnd}}).then((res) => {
                alert(res.data.result);
            });
        },
        stopAll: function () {
            let url = "auction/stopAll";
            axios.get(url).then((res) => {
                alert(res.data.result);
            });
        },
        showImg: function (hwnd) {
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
    }
});