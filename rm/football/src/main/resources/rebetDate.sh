db.transactionDate.find().forEach(function(z){db.transaction.save(z);})
db.betDate.find().forEach(function(z){db.bet.save(z);})