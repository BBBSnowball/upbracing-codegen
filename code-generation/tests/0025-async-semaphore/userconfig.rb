# JTAG Ice on usb, for AT90CAN128
AVRDUDE = "avrdude -cjtag2 -pc128 -Pusb"

# Atmel mkII (or compatible device) on usb, for AT90CAN128
#AVRDUDE = "avrdude -cjtag2 -pc128 -Pusb"

# use any of the ttyUSB ports (Linux)
#SERIALPORT = lambda { Dir["/dev/ttyUSB*"].first }

# use COM10 (Windows)
SERIALPORT = "COM10"

# use color in terminal ?
#COLOR=false	# no color
#COLOR=true		# use 16 colors, always
COLOR=:auto		# try to recognize correct setting
#COLOR=256		# use 256 colors

# user interface
UI = :simple	# text
#UI = :ncurses	# text UI with windows (only for Linux)
#UI = :swing	# graphical, not finished -> Krishna
