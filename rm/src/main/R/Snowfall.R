library(snowfall)

# cl <- makeCluster(4, type = "SOCK") 

# a <- matrix(c(1),ncol=4000,nrow=4000)

# parMM(cl,a,a)
# parMM(cl,a,a)
# stopCluster(cl)
a <- "a"
class(a)
lastCost <- 10
thisCost <- 20
alpha <- 10
if(lastCost < thisCost)
        alpha <- alpha * 0.8

print(paste("slower alpha to",alpha))        
