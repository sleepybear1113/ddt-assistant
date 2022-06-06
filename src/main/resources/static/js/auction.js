let auctionApp = new Vue({
    el: '#auction',
    data: {
        auctionList: {
            auctionItemList: [],
            hwnd: 0,
        },
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
            this.auctionList.auctionItemList.push(itemSample);
        },
        deleteItem: function (index) {
            this.auctionList.auctionItemList.splice(index, 1);
        },
        getItems: function () {
            let url = "auction/getList";
            axios.get(url).then((res) => {
                this.auctionList = new AuctionList(res.data.result);
            });
        },
        savaItems: function () {
            let url = "auction/update";
            axios.post(url, this.auctionList).then(() => {
                alert("ok");
            });
        },
        sell: function () {
            let url = "auction/bindAndSell";
            axios.get(url, {params: {hwnd: this.hwnd}}).then((res) => {
                alert(res.data.result);
            });
        },
        stop: function () {
            let url = "auction/stop";
            axios.get(url, {params: {hwnd: this.hwnd}}).then((res) => {
                alert(res.data.result);
            });
        },
    }
});