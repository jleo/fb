m = function () {
    emit({cid:this.cid,matchId:this.matchId}, 1)
}
r = function (k, vals) {
    return Array.sum(vals);
}
res = db.result.mapReduce(m,r, { out : {replace:"matchDupe"} });

db.matchDupe.find({value: {$gt: 1}}).forEach(
    function(obj) {
        var cur = db.result.find({ cid:obj._id.cid,matchId:obj._id.matchId });
        var first = true;
        while (cur.hasNext()) {
            var doc = cur.next();
            if (first) {first = false; continue;}
            db.result.remove({ _id: doc._id });
        }
    })