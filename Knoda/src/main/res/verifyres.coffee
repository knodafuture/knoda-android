fs = require 'fs'
_ = require('underscore');

baselinefiles = fs.readdirSync './drawable-xhdpi'

xxhpdifiles = fs.readdirSync './drawable-xxhdpi'

hpdifiles = fs.readdirSync './drawable-hdpi'


filesnotinxxhdpi = _.difference(baselinefiles,xxhpdifiles)
filesnotinhpdi = _.difference(baselinefiles, hpdifiles)


console.log "Using drawable-xhpdi as baseline. #{baselinefiles.length} files found."

if filesnotinxxhdpi.length > 0
	console.log "Missing #{filesnotinxxhdpi.length} files in drawable-xxhdpi."
	console.log "Files: #{JSON.stringify(filesnotinxxhdpi)}"

if filesnotinhpdi.length > 0
	console.log "Missing #{filesnotinhpdi.length} files in drawable-hdpi."
	console.log "Files: #{JSON.stringify(filesnotinhpdi)}"


filesinxxhpdi = _.difference(xxhpdifiles, baselinefiles)

if filesinxxhpdi.length > 0
	console.log "#{filesinxxhpdi.length} extra files in drawable-xxhdpi."
	console.log "Files: #{JSON.stringify(filesinxxhpdi)}"

filesinhpdi = _.difference(hpdifiles, baselinefiles)

if filesinhpdi.length > 0
	console.log "#{filesinhpdi.length} extra files in drawable-hdpi"
	console.log "Files: #{JSON.stringify(filesinhpdi)}"

# fs.readdir '.', (err, files) ->
# 	console.log files
# 	for file in files
# 		if file is 'renameandroidassets.coffee' or file is ''
# 			continue

# 		underscore = file.indexOf '_'

# 		unless underscore and file.indexOf 'dpi'
# 			continue

# 		words = file.substring(0, underscore).split /(?=[A-Z])/

# 		outfile = words.join('_').toLowerCase() + '.png'

# 		fs.renameSync file, outfile
