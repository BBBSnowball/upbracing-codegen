t = $config.add_table "valve_data"
t.add_column "voltage", "int8_t"
t.add_column "position", "uint8_t"

t.add_data -5,   0
t.add_data  3,  40
t.add_data 10,  55
t.add_data 24, 100

def load_csv(name, file, types = {})
  table = $config.add_table name
  File.open file do |f|
    # read the first line: it contains the names
    header = f.readline.split ","
    header.each do |name|
      # remove whitespace
      name = name.strip
      
      # get type or use default
      type = (types[name] || "int")
      
      # add it
      table.add_column name, type
    end
    
    # read the data lines
    f.each_line do |line|
      values = line.strip.split /\s*,\s*/
      puts values.inspect
      table.add_data *values unless values.empty?
    end
  end
end

load_csv("valve_data2", "valve_data.csv", "voltage" => "int8_t")


require 'csv'

def load_csv2(name, file, types = {})
  table = $config.add_table name
  CSV.foreach(file) do |row|
    if table.names.empty?
      # This is the first row
      row.each do |name|
        # remove whitespace
        name = name.strip
        
        # get type or use default
        type = (types[name] || "int")
        
        # add column
        table.add_column name, type
      end
    else
      # remove whitespace
      row = row.map { |x| x.strip }
      
      # add the row
      table.add_data *row unless row.empty?
    end
  end
end

load_csv2("valve_data3", "valve_data.csv", "voltage" => "int8_t")
