def parse = Date.parse("yyyy-MM-dd HH:mm:ss", '2013-01-9 10:02:20')
println parse.clearTime()
println parse