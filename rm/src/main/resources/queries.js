db.transactionDate.find({},{delta:1,matchId:1,resultRA:1,resultRB:1,betOn:1,"betInfo.typeDispaly":1,"betInfo.betOn":1})

db.transaction.find({},{delta:1,matchId:1,resultRA:1,resultRB:1,betOn:1,"betInfo.typeDispaly":1,"betInfo.betOn":1})


var sum=0;
db.transactionDate.find().forEach(function(z){sum+=z.delta;})