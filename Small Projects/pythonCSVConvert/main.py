import csv


def main():
    while True:
        choose = input('1. txt to CSV \n2. CSV to txt\n')

        while True:
            try:
                file_loc = input('What is the file directory?\n')
                file = open(file_loc, 'r')
                file_content = file.read()
                break
            except:
                print('file not found')

        file_print = input('Where is file output location? (must end in .csv)\n')

        if choose == '1':
            delim = input('What is your delimiter?\n')

            data_1 = file_content.split('\n')

            data_2 = []
            for i in data_1:
                data_2.append(i.split(delim))

            with open(file_print, 'w', newline='') as csv_file:
                csv_writer = csv.writer(csv_file)
                csv_writer.writerows(data_2)

            break

        elif choose == '2':
            sep = input('What is your separator?\n')

            with open(file_print, 'w', newline='') as writefile:
                content = file_content.replace(',', sep)
                writefile.write(content)
                writefile.close()

            break


main()
