# library(ff)
print("cena")
options(max.print=999)
setwd("/Users/jleo")
# gamePlayer <- read.csv(file="/Users/jleo/Dropbox/nba/meta/gamePlayer.txt",header = T,sep = ",")
# gameInfo <- read.csv(file="/Users/jleo/Dropbox/nba/meta/gameInfo.txt",header = T,sep = ",")
# gameList <- read.csv(file="/Users/jleo/Dropbox/nba/meta/gameList.txt",header = T,sep = ",")

trainingSample <- matrix(scan("/Users/jleo/Dropbox/nba/meta/small.txt", n = 8546*120057),8546, 120057, byrow = TRUE)
# read.table(file="/Users/jleo/Dropbox/nba/meta/small.txt",sep = ",")
print(class(trainingSample))
# cluster <- makeCluster(1, type = "SOCK")

# trainingSample <- as.matrix(trainingSample)

m <- dim(trainingSample)[1]

grad <- function(x, y, theta) {
	
  gradient <- (1/m)* (crossprod(x, x %*% t(theta) - y))
  return(t(gradient))
}
 
# define gradient descent update algorithm
grad.descent <- function(x, y, maxit){
    theta <- matrix(data=c(0),nrow=1,ncol=dim(trainingSample)[2])
 
    alpha = 1
    lastCost <- 100000000
    for (i in 1:maxit) {
      theta <- theta - alpha  * grad(x, y, theta)   
      thisCost <- cost(x,y,theta)
      print(thisCost)
      if(lastCost < thisCost){
        alpha <- alpha * 0.8
        print(paste("slower alpha to",alpha))
      }
      write.table(theta, "/Users/jleo/Dropbox/nba/meta/theta.txt", quote=F, row.names = F,
                   col.names = F)
      lastCost <- thisCost
    }
 	return(theta)
}

cost <- function(x,y,theta){
	return(sum((x%*%t(theta) - y)^2))
}

y <- trainingSample[,1]
x <- cbind(matrix(data=c(1),nrow=m,ncol=1), trainingSample[,2:(dim(trainingSample)[2])])

# summary(lm(y ~ x[, 2:dim(trainingSample)[2]]))
grad.descent(x,y,1000000)
# stopCluster(cl)
#plot(y~x[,2])