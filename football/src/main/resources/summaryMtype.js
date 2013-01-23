m = function () {

    var aid = this.betInfo.aid;
    var matchId = this.matchId;
    var mtype = db.result.findOne({matchId:matchId},{mtype:1}).get("mtype");
    emit({aid:aid,mtype:mtype}, {net:this.delta,bet:this.bet,count:1});
}

r = function (k, vals) {
    var totalBet = 0
    var net = 0;
    var totalCount=0;

    for ( var i=0; i<vals.length; i++){
        net+=vals[i].net;
        totalBet+= vals[i].bet;
        totalCount += vals[i].count
    }

    return {net:net,bet:totalBet,count:totalCount};
}

f = function (key, reducedValue) {
    reducedValue.betAverage = reducedValue.totalBet/reducedValue.count;
    reducedValue.profitAverage = reducedValue.net/reducedValue.count;
    return reducedValue;
};

res = db.transactionDate.mapReduce(m,r, {finalize:f, out : "summaryDateMtype"} );