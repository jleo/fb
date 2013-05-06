library(snowfall)

# cl <- makeCluster(4, type = "SOCK") 

# a <- matrix(c(1,2,3,4,5),ncol=4,nrow=5)


# a[a[,1] > 3 & a[,1] <5,]
# parMM(cl,a,a)
# parMM(cl,a,a)
# stopCluster(cl)

trainingSample <- matrix(scan("/Users/jleo/Dropbox/nba/meta/modified.txt", n = 1*120057),1, 120057, byrow = TRUE)
rm(trainingSample)
gc()