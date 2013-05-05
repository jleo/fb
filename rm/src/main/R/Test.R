theta <- matrix(scan("/Users/jleo/Dropbox/nba/meta/theta.txt", n = 1*120057),1, 120057, byrow = TRUE)

n <- 1

trainingSample <- matrix(scan("/Users/jleo/Dropbox/nba/meta/testing2.txt", n = n*120057),n, 120057, byrow = TRUE)

m <- dim(trainingSample)[1]

sample <- cbind(matrix(data=c(1),nrow=m,ncol=1), trainingSample[,2:(dim(trainingSample)[2])])

print(sample %*% t(theta))