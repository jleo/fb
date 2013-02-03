m=function(){
    var ch = this._id.ch;

    emit({ch:ch}, {net:this.value.net, aid:this._id.aid, count:this.value.count});
}

r=function(key, emits){
    var topNet = -9999999;
    var topAid = "empty";
    var count = 0;

    for (var i in emits){
        if (emits[i].net > topNet){
            topNet = emits[i].net;
            topAid = emits[i].aid;
            count = emits[i].count;
        }
    }
    return {net: topNet, aid:topAid, count: count};
}

rm = db.summaryDateByCh.mapReduce(m, r, {out: {replace: "topChNet"}})