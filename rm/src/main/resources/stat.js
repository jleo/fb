db.result.find().forEach(function(z){var returns=(1*z.w1*z.p1*z.l1/(1*z.w1*z.1*p1+z.p1*w.l1+1*z.l1*z.w1));z.returnRate=returns;db.result.save(z);})


db.result.find().forEach(function(z){z.wp = z.returnRate/z.w1;z.pp = z.returnRate/z.p1;z.lp = z.returnRate/z.l1;db.result.save(z);})

db.result.find().forEach(function(z){z.wp = z.returnRate/z.w1;z.pp = z.returnRate/z.p1;z.lp = z.returnRate/z.l1;db.result.save(z);})

m = function () {
    var sumScore = 0;
    var date = new Date(this.time)
    var year = date.getFullYear();
    var month = date.getMonth()+1;

    if(this.wp*1 >= this.pp*1 && this.wp*1 >= this.lp*1 && this.result == 1){
        sumScore+=1;
        sumScore+=(1-this.lp*1);
        sumScore+=(1-this.pp*1)/2;
    }
    if(this.lp*1 >= this.pp*1 && this.lp*1 >= this.wp*1 && this.result == -1){
        sumScore+=1;
        sumScore+=(1-this.wp*1);
        sumScore+=(1-this.pp*1)/2;
    }
    if(this.pp*1 >= this.wp*1 && this.pp*1 >= this.lp*1 && this.result == 0){
        sumScore+=1;
        sumScore+=(1-this.wp*1)/2;
        sumScore+=(1-this.pp*1)/2;
    }

    emit({cid:this.cid,mtype:this.mtype,year:year}, {sumScore:sumScore,total:1});
}

r = function (k, vals) {
    var sumScore = 0
    var total = 0;

    for ( var i=0; i<vals.length; i++){
        total+=vals[i].total;
        sumScore+= vals[i].sumScore;
    }

    return {sumScore:sumScore,total:total};
}

f = function (key, reducedValue) {

    reducedValue.average = reducedValue.sumScore/reducedValue.total;

    return reducedValue;
};

res = db.result.mapReduce(m,r, {finalize: f, out : "resultcidmonth"} );


db.resultcidFlat.find({mtype:/科威盃/,total:{$gt:50}}).sort({average:-1})