let introductionApp = new Vue({
    el: '#introduction',
    data: {
        tabIndex: 0,
        tabs: [
            {name: "简介"},
            {name: "请我吃点东西"},
            {name: "工程介绍"},
            // {name: "性能"},
        ],
        initEchart: false,
        maxMemoryUse: 0,
        currentMemoryUse: 0,
    },
    created() {
    },
    methods: {
        changeTab: function (index) {
            this.tabIndex = index;
            if (index === 3 && !this.initEchart) {
                this.showMemoryUse();
                this.initEchart = true;
            }
        },
        showMemoryUse: function () {
            let myChart = echarts.init(document.getElementById("memory-use"));
            let url = "system/getMemoryUse";
            axios.get(url).then((res) => {

                let data = new MemoryUseList(res.data.result);
                this.maxMemoryUse = (data.maxMemoryUse / 1024).toFixed(2);
                this.currentMemoryUse = (data.currentMemoryUse / 1024).toFixed(2);
                let option = {
                    title: {text: "内存使用历史"},
                    tooltip: {},
                    legend: {data: ["内存占用"]},
                    xAxis: {
                        type: "time",
                        name: "时间"
                    },
                    yAxis: {name: "内存/MB"},
                    series: [
                        {
                            name: "内存占用",
                            type: "line",
                            data: data.getData(),
                        }
                    ]
                };
                myChart.setOption(option);
            });
        },
    }
});