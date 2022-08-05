let otherFunction = new Vue({
    el: '#otherFunction',
    data: {
        selectedTab: "host文件查看/修改",
        tabs: [
            {name: "host文件查看/修改"},
            {name: "自动刷点券"},
        ],
        isAdmin: false,
        host: "",
    },
    created() {
        this.testAdmin();
        this.getHost();
    },
    methods: {
        changeTab: function (tab) {
            this.selectedTab = tab.name;
        },
        testAdmin: function () {
            let url = "system/testAdmin";
            axios.get(url).then((res) => {
                this.isAdmin = res.data.result === true;
            });
        },
        getHost: function () {
            let url = "system/getHost";
            axios.get(url).then((res) => {
                this.host = res.data.result.string;
            });
        },
        updateHost: function () {
            let url = "system/updateHost";
            axios
            axios.post(url, this.host, {
                headers: {"Content-Type": "text/plain"},
            }).then((res) => {
                showInfo(res.data.result);
            });
        },
    }
});