let introductionApp = new Vue({
    el: '#introduction',
    data: {
        selectedTab: "简介",
        tabs: [
            {name: "简介"},
            {name: "请我吃点东西"},
            {name: "工程介绍"},
        ],
    },
    created() {
    },
    methods: {
        changeTab: function (tab) {
            this.selectedTab = tab.name;
        },
    }
});