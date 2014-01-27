fs = require 'fs'

fs.readdir '.', (err, files) ->
	console.log files
	for file in files
		if file is 'renameandroidassets.coffee' or file is ''
			continue

		underscore = file.indexOf '_'

		unless underscore and file.indexOf 'dpi'
			continue

		words = file.substring(0, underscore).split /(?=[A-Z])/

		outfile = words.join('_').toLowerCase() + '.png'

		fs.renameSync file, outfile
