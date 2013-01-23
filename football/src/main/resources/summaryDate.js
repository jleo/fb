m = function () {
    var aid = this.betInfo.aid;
    var mtype = this.betInfo.matchTime;
    var year = mtype.getFullYear();
    var month = mtype.getMonth();
    var day = mtype.getDate();

    emit({aid: aid,year:year,month:month,day:day}, {net: this.delta, bet: this.bet, count: 1,correct: this.delta > 0?1:0, push:this.delta==0?1:0,lose:this.delta<0?1:0,total:1});
}
r = function (k, vals) {
    var totalBet = 0
    var net = 0;
    var totalCount = 0;
    var correct = 0
    var push = 0;
    var lose = 0;
    var total = 0;
    for (var i = 0; i < vals.length; i++) {
        net += vals[i].net;
        totalBet += vals[i].bet;
        totalCount += vals[i].count
        correct += vals[i].correct;
        push += vals[i].push;
        lose += vals[i].lose;
        total += vals[i].total;
    }
    return {net: net, bet: totalBet, count: totalCount,lose: lose, correct: correct, push: push,total:total};
}
f = function (key, reducedValue) {
    reducedValue.betAverage = reducedValue.bet / reducedValue.count;
    reducedValue.profitAverage = reducedValue.net / reducedValue.count;
    reducedValue.correctRate = reducedValue.correct / reducedValue.total;
    reducedValue.pushRate = reducedValue.push / reducedValue.total;
    reducedValue.loseRate = reducedValue.lose / reducedValue.total;
    return reducedValue;
};
res = db.transactionDate.mapReduce(m, r, {finalize: f, out: {replace:"summaryDateByDate"}});