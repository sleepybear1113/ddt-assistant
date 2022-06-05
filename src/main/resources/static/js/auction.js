let auctionApp = new Vue({
    el: '#auction',
    data: {
        auctionList: {
            auctionItemList: [],
        },
        itemSample: {
            enabled: false,
            name: "",
            ocrName: "",
            argueUnitPrice: 1.0,
            mouthfulUnitPrice: 1.0,
            minNum: 1,
            auctionTime: "48",
        },
    },
    created() {
        this.getItems();
    },
    methods: {
        addNewItem: function () {
            this.auctionList.auctionItemList.push(this.itemSample);
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
    }
});