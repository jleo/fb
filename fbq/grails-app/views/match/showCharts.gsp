<%--
  Created by IntelliJ IDEA.
  User: shangrz
  Date: 13-1-26
  Time: 下午8:55
  To change this template use File | Settings | File Templates.
--%>


<%@ page import="org.Match" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">

    <title>图表</title>

</head>
<body>
<script src="${resource(dir: 'js', file: 'highcharts.js')}" type="text/javascript"></script>

<div>

    <div id="container" style="width: 100%; height: 400px">1</div>
     %{--${matchInstanceJson}--}%
</div>
<script type="text/javascript">
    $(function () {
        var chart;
        $(document).ready(function() {
            chart = new Highcharts.Chart({
                chart: {
                    renderTo: 'container',
                    type: 'line',
                    marginRight: 130,
                    marginBottom: 25
                },
                title: {
                    text: '${team}${where}表现',
                    x: -20 //center
                },
                subtitle: {
                    text: '最近${params.max}${where}',
                    x: -20
                },
                xAxis: {
                    type: 'datetime',
                    labels: {
                        formatter: function() {
                            return  Highcharts.dateFormat('%Y-%m-%d', this.value);
                        }
                    }
                    %{--categories:  ${times}--}%
                },
                yAxis: {
                    title: {
                        text: '进球数'
                    },
                    plotLines: [{
                        value: 0,
                        width: 1,
                        color: '#808080'
                    }]
                },
                tooltip: {
                    formatter: function() {
                        return '<b>'+ this.series.name+' : '+this.y+'球'+'</b><br/>'+
                                this.point.teamA + ' : '+this.point.teamB +'<br/>'+
                                  this.point.Score+'<br/>' +'时间： '+Highcharts.dateFormat('%Y-%m-%d', this.x)+'<br/>' ;


                    }
                },
                legend: {
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    x: -10,
                    y: 100,
                    borderWidth: 0
                },
                series: [{
                    name: '主队进球',
                    data: ${teamAGoals}
                } ,  {
                    name: '客队进球',
                    data:  ${teamBGoals}
                } ,
                    {
                       name:"${params.visiting?'客队-主队':'主队-客队'}进球差",
                       data:${rs}


                    }

            ]
            });
        });

    });

</script>

</body>
</html>
