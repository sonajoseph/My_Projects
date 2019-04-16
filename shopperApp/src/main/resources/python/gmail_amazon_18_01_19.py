from __future__ import print_function
from __future__ import unicode_literals
import base64
import email
import json
import re


def msg(d, sub, fm, a):
    global currentStatus
    qty=1
    date_x = d
    subjct = sub
    frm_id = str(fm)
    message = str(a)
    vendor = 'Amazon'
    mail_list = ['"Amazon.in" <order-update@amazon.in>', '"Amazon.in" <shipment-tracking@amazon.in>', '"Amazon.in" <auto-confirm@amazon.in>','"order-update@amazon.in" <order-update@amazon.in>']
    if frm_id in mail_list:
        try:
            date = date_x                
        except:
            date = 'Date is not available!!!'

        try:
            sub_msg_1 = subjct
            rgx_5 = re.findall('Your Amazon.in order',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Ordered'
                
            rgx_5 = re.findall('Dispatched',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Shipped'
            rgx_5 = re.findall('Arriving today',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Arriving today'
            rgx_5 = re.findall('Delivered',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Delivered'
                
            rgx_5 = re.findall('has been cancelled',sub_msg_1)
            if (len(rgx_5)>0):
                print('in cancelled')
                currentStatus = 'Cancelled'
                fromMailId = frm_id.split('<')[1].split('>')[0]
                try:
                    msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
                    final_msg = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
                    orderNumber = final_msg.split('Order #')[1].split('\r\n')[0]
                    orderNumber = orderNumber.strip()
                except:
                    orderNumber = 'Not available'
                data = [{"fromMailId" : fromMailId, "date" : date,"currentStatus" : currentStatus,"orderNumber" : orderNumber,"vendor" : vendor, }]
                print(data)
                return json.dumps(data)
                #######################################################    Updationn
            rgx_5 = re.findall('Cancelled:',sub_msg_1)
            if (len(rgx_5)>0):
                print('in cancelled')
                currentStatus = 'Cancelled'
                fromMailId = frm_id.split('<')[1].split('>')[0]
                try:
                    msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
                    final_msg = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
                    orderNumber = final_msg.split('Order #')[1].split('\r\n')[0]
                    orderNumber = orderNumber.strip()
                except:
                    orderNumber = 'Not available'
                data = [{"fromMailId" : fromMailId, "date" : date,"currentStatus" : currentStatus,"orderNumber" : orderNumber,"vendor" : vendor, }]
                print(data)
                return json.dumps(data)
        except:
            currentStatus = 'Subject not available!!!'

        try:
            fromMailId = frm_id.split('<')[1].split('>')[0]                 
        except:
            fromMailId = 'Mail From is not available!!!'


        try:   
            toMaild = to_id.split('<')[1].split('>')[0]             
        except:
            toMaild = 'Mail To not available!!!'

        ############################          Amazon Delivery mail        #############################
        mailsep = re.findall('"Amazon.in" <order-update@amazon.in>',frm_id)
        if (len(mailsep)>0):
            currentStatus = 'Delivered'
            rgx_7 = re.compile('Delivered: ')
            try:
                item = rgx_7.split(sub_msg_1)[1]
                msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
                final_msg = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
                rgx_8 = re.compile('Order #')
                orderNumber = rgx_8.split(final_msg)[1].split('.')[0].replace('\r\n','').replace("https://www", "")
                data = [{"fromMailId" : fromMailId, "date" : date,"currentStatus" : currentStatus,"orderNumber" : orderNumber,"vendor" : vendor }]
#                 print(data) 
                data=json.dumps(data)
                print(data)
                return data
            except:
                data = 'No Data'
                print(data)
                return json.dumps(data)

        ############################          Amazon Dispatched & Arraival day mail        #############################
        mailsep = re.findall('"Amazon.in" <shipment-tracking@amazon.in>',frm_id)
        if (len(mailsep)>0):

            # mailsep_2 = re.findall('Arriving today:',sub_msg_1)
            # if (len(mailsep_2>0)):
            #     currentStatus = 'Arriving today'
            #     rgx_7 = re.compile('Arriving today: ')
            #     item = rgx_7.split(sub_msg_1)[1]
            #     msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
            #     final_msg = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
            #     rgx_8 = re.compile('Order #')
            #     orderNumber = rgx_8.split(final_msg)[1].split('.')[0]
            #     data = 'No Data'
            #     return json.dumps(data)  
            mailsep_3 = re.findall('Dispatched:',sub_msg_1)
            if (len(mailsep_3)>0):

                currentStatus = 'Shipped'
                try:
                    # msg_str = base64.urlsafe_b64decode(message['payload']['parts'][0]['body']['data'].encode('UTF8'))
                    msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
                    final_msg = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
                    total_prodct_count = re.findall('Order #',final_msg)
                    body_order_count = int(len(total_prodct_count))

                    if (body_order_count == 1):

                        # qty = body_order_count

                        #####  Amazon.in Dispatched or shipping Confirmation  #####
                        try:
                            rgx_1 = re.compile('Order #')
                            orderNumber = rgx_1.split(final_msg)[1].split('\n')[0].replace('\r','')
                            orderNumber = orderNumber.strip()
                            
                        except:
                            orderNumber = 'Error in odder_ID'

                        try:
                            rgx_1 = re.compile('Your estimated delivery date is:')
                            arrivesOn = rgx_1.split(final_msg)[1].split('Track')[0].replace('\n','').replace('   ','').replace('\r','')
                            
                        except:
                            arrivesOn = 'Not available'

                        try:
                            rgx_1 = re.compile('was sent to:')
                            deliveryAddress = rgx_1.split(final_msg)[1].split('Your')[0].replace('\r\n','')#.replace('\n','')
                            deliveryAddress = deliveryAddress.strip()
                            
                        except:
                            deliveryAddress = 'Not available'

                        try:
                            rgx_1 = re.compile('Shipment Details')
                            shpmnt_prdct_dtls_whole = rgx_1.split(final_msg)[1].split('---------------------')[0].replace('\r\n','').replace('    ','')
                            
                            #### Code to get the exact product name(cleaned). 
                            item = shpmnt_prdct_dtls_whole.split('Sold')[0]
                            item_1=item.replace(",","").replace(".","").replace('-'," ").replace('&','').replace('&amp;',' and ').replace(';'," ").replace('amp',"and").replace('(s):','')
                            
                            rgx_4 = re.findall(' x ',item_1)
                            if (len(rgx_4) > 0):
                                rgx_4 = re.compile(' x ')
                                qty_1 = rgx_4.split(item_1)[0]
                                qty = qty_1.strip()
                                item_1 = rgx_4.split(item_1)[1]
                            item = item_1.strip()
                            
                            #### Code to get the exact product price from shpmnt_prdct_dtls_whole (cleaned).
                            subTotal = 'Rs.' + shpmnt_prdct_dtls_whole.split('Rs.')[1]
                            subTotal = subTotal.strip()
                            
                        except:
                            item = 'Not available'

                        try:
                            rgx_1 = re.compile('Shipment Total: ')
                            grandTotal_1 =  rgx_1.split(final_msg)[1].split('\n')[0]#.replace('.\r','')#.replace('\n','')
                            try:
                                rgx_1 = re.compile('=')
                                grandTotal = rgx_1.split(grandTotal_1)[0].replace('\r','').replace('\n','')
                                grandTotal = grandTotal.strip()
                            except:
                                   grandTotal = grandTotal_1.replace('\r','').replace('\n','')
                                   grandTotal = grandTotal.strip()
                                
                        except:
                            grandTotal = 'Not available'
                    data = [{ "fromMailId" : fromMailId, "date" : date, "currentStatus" : currentStatus, "arrivesOn" : arrivesOn, "orderNumber" : orderNumber, "orderItemsList":[{"item" : item, "qty" : qty, "subTotal" : subTotal }],"vendor" : vendor, "grandTotal" : grandTotal, "deliveryAddress" : deliveryAddress,}]

                except:
                    data = 'No Data'
                    print(data)
                    return json.dumps(data)
                print(data)
                return json.dumps(data)

        mailsep = re.findall('"Amazon.in" <auto-confirm@amazon.in>',frm_id)

        if (len(mailsep)>0):
            print('in if loop')
            msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
            final_msg = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
            total_prodct_count = re.findall('Order #',final_msg)
            body_order_count = int(len(total_prodct_count))

            ###### Order confirmation or placed amazon #####
            if(body_order_count>1):
                num_of_prdcts =(body_order_count/2)

                # qty = num_of_prdcts
                if (num_of_prdcts >1):
                    xxx = final_msg.split('Order #')

                    orderNumber = []
                    data = []
                    qty = 1

                    for i in range(num_of_prdcts+1,body_order_count+1):


                        ddaattaa = xxx[i:][0]
                        try:
                            orderNumber = ddaattaa.split('\r')[0].replace('\r','')
                            orderNumber = orderNumber.strip()
                        except:
                            orderNumber = 'Not available'

                        try:
                            rgx_1 = re.compile('Arriving:')
                            arrivesOn = rgx_1.split(ddaattaa)[1].split('Your')[0].replace('\r\n','').replace(' ','').replace('*','')     
                        except:
                            arrivesOn = 'Not available'
#                         print(arrivesOn)

                        try:
                            rgx_1 = re.compile('will be sent to:')
                            #deliveryAddress = rgx_1.split(ddaattaa)[1].split('*\r\n')[0]#.replace('\r\n','').replace('*','')
                            deliveryAddress = rgx_1.split(ddaattaa)[1].split('Ordered')[0].replace('\r\n','').replace('*','')
                            deliveryAddress = deliveryAddress.strip()
                            deliveryAddress = deliveryAddress.replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ')
                            
                        except:
                            deliveryAddress = 'Not available'


                        try:
                            rgx_1 = re.compile('item_image\r')
#                             print (ddaattaa)
                            prdct_data_1 = rgx_1.split(ddaattaa)[1].split('<https')[0].replace('(s):','')
                            item_1 = prdct_data_1.replace('\n:','').replace('\r\n',' ').replace('&amp;',' and ').replace(",","").replace('"',"").replace('-'," ").replace('&','').replace('(s):','')
                            
                            rgx_4 = re.findall("\r\n\s+[0-9]+\sx\s",prdct_data_1)
                            if (len(rgx_4) > 0):
                                rgx_4 = re.compile(' x ')
                                qty_1 = rgx_4.split(item_1)[0]
                                qty = qty_1.strip()
                                item_1 = rgx_4.split(item_1)[1]
                            item = item_1.strip()
                            item = item.replace('     ',' ').replace('     ',' ').replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ')
                        except:
                            rgx_4 = re.compile("Ordered item")
#                           print (ddaattaa)
                            prdct_data_1 = rgx_4.split(ddaattaa)[1].split('Rs.')[0].replace('(s):','')
                            item_1 = prdct_data_1.replace('\r\n',' ').replace('&amp;',' and ').replace(",","").replace('"',"").replace('-'," ").replace('&','').replace('\n:','').replace('(s):','')
                            
                            rgx_4 = re.findall("\r\n\s+[0-9]+\sx\s",prdct_data_1)
                            if (len(rgx_4) > 0):
                                rgx_4 = re.compile(' x ')
                                qty_1 = rgx_4.split(item_1)[0]
                                qty = qty_1.strip()
                                item_1 = rgx_4.split(item_1)[1]
                            item = item_1.strip()
                            item = item.replace('     ',' ').replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ').replace('  ',' ')
#                             item = 'Not available'
                        try:
                            rgx_1 = re.compile('Ordered item')
                            subTotal_1 = rgx_1.split(ddaattaa)[1].split('Sold by:')[0]
                            rgx_11 = re.compile('Rs.')
                            subTotal_11 = rgx_11.split(str(subTotal_1))[1].split('\r\n')[0]
                            subTotal = 'Rs.'+subTotal_11
                        except:
                            subTotal = 'Not available'

                        try:
                            rgx_1 = re.compile('Order Total:')
                            grandTotal_1 =  rgx_1.split(ddaattaa)[1].split('Payment')[0]
                            try:
                                rgx_1 = re.compile('=')
                                grandTotal = rgx_1.split(grandTotal_1)[0]
                                grandTotal = grandTotal.strip() 
                            except:
                                   grandTotal = grandTotal_1
                                   grandTotal = grandTotal.strip()
                            
                        except:
                            grandTotal = 'Not available'

                        data_1 = { "fromMailId" : fromMailId, "date" : date, "currentStatus" : currentStatus, "arrivesOn" : arrivesOn, "orderNumber" : orderNumber, "orderItemsList":[{"item" : item, "qty" : qty, "subTotal" : subTotal }],"vendor" : vendor, "grandTotal" : grandTotal, "deliveryAddress" : deliveryAddress,}
                        data.append(data_1)
                    print('Data',data) 
                    return json.dumps(data)


                if (num_of_prdcts == 1):
                    try:
                        rgx_1 = re.compile('Order #')
                        orderNumber = rgx_1.split(final_msg)[1].split('\n')[0].replace('\r','')
                        orderNumber = orderNumber.strip()
                        
                    except:
                        orderNumber = 'Not available'
                
                    try:
                        rgx_1 = re.compile('Arriving:')
                        arrivesOn = rgx_1.split(final_msg)[1].split('Your')[0].replace('\r\n','').replace(' ','')
                        
                    except:
                        arrivesOn = 'Not available'
                
                    try:
                        rgx_1 = re.compile('will be sent to:')
                        deliveryAddress = rgx_1.split(final_msg)[1].split('====')[0].replace('\r\n','').replace('          ',' ').replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ')
                        deliveryAddress = deliveryAddress.strip()
                        deliveryAddress = deliveryAddress.replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ').replace('  ',' ') 
                        
                    except:
                        deliveryAddress = 'Not available'
                
                    try:
                        rgx_1 = re.compile('Order Total:')
                        grandTotal_1 =  rgx_1.split(final_msg)[1].split('Payment')[0].replace('\r\n','')
                        try:
                            rgx_1 = re.compile('=')
                            grandTotal = rgx_1.split(grandTotal_1)[0]
                            grandTotal = grandTotal.strip()
                        except:                            
                               grandTotal = grandTotal_1
                               grandTotal = grandTotal.strip()
                    except:
                        grandTotal = 'Not available'
                    
                    #### Ordered item details ####
                    
                    rgx_4 = re.compile("Ordered item")
                    ordr_items_no_1 = rgx_4.split(final_msg)[1].split('\r\n\r\n_')[0].replace("(s):",'')
                
                    total_no_of_prdcts_1 = len(re.findall('Rs.',ordr_items_no_1))  
                
                    seprtr = ordr_items_no_1.split('Sold by:')
                
                    ### contains the listed number of products after splited through for loop ###########
                    data_11 = []
                    
                    for i in range(len(seprtr)-1):
                
                        data_12 = seprtr[i]
                        # print(data_12)
                        data_11.append(data_12)

                    # "orderItemsList":[{"item" : item, "qty" : qty, "subTotal" : subTotal }]
                    orderItemsList = []
                    qty = 1 
                    for i in range(0,1):
                
                        prdct_data_1 = data_11[i]
                
                        rgx_11 = re.compile('\r\n')
                        item_1 = rgx_11.split(prdct_data_1)[1].split('\r\n')[0]
                        item_1=item_1.replace(",","").replace('"',"").replace('-'," ").replace('&','').replace('&amp;',' and ').replace(';'," ").replace('amp',"and").replace('(s):','')
                
                        rgx_4 = re.findall("\r\n\s+[0-9]+\sx\s",prdct_data_1)
                        if (len(rgx_4) > 0):
                            rgx_4 = re.compile(' x ')
                            qty_1 = rgx_4.split(item_1)[0]
                            qty = qty_1.strip()
                            item_1 = rgx_4.split(item_1)[1]

                        item = item_1.strip()
                        item = item.replace('     ',' ').replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ').replace('  ',' ')

                        rgx_11 = re.compile('Rs.')
                        price_1 = 'Rs.'+ rgx_11.split(prdct_data_1)[1].split('\r')[0]
                        price_1 = price_1.strip()
                
                        mult_prdct = {"item" : item_1, "subTotal" : price_1 ,"qty":qty }
                        orderItemsList.append(mult_prdct)
                    
                
                    for i in range(1,len(data_11)+1):
                        try:
                            prdct_data_1 = data_11[i]
                            rgx_11 = re.compile('\r\n\r\n')
                            item_1 = rgx_11.split(prdct_data_1)[1].split('\r\n')[0]#.encode('ascii', 'ignore')
                            
                            item_1=item_1.replace(",","").replace('"',"").replace('-'," ").replace('&','').replace('&amp;',' and ').replace(';'," ").replace('amp',"and").replace('(s):','')
                            
                            rgx_4 = re.findall("\r\n\r\n\s+[0-9]+\sx\s",prdct_data_1)
                            if (len(rgx_4) > 0):
                                rgx_4 = re.compile(' x ')
                                qty_1 = rgx_4.split(item_1)[0]
                                qty = qty_1.strip()
                                item_1 = rgx_4.split(item_1)[1]
                            item = item_1.strip()
                            item = item.replace('     ',' ').replace('      ',' ').replace('     ',' ').replace('    ',' ').replace('   ',' ').replace('  ',' ')
                            
                            rgx_11 = re.compile('Rs.')
                            price_1 = 'Rs.' + rgx_11.split(prdct_data_1)[1].split('\r')[0]
                            price_1 = price_1.strip()
                            mult_prdct = {"item" : item_1, "subTotal" : price_1,"qty":qty }
                            orderItemsList.append(mult_prdct)
                        except:
                            pass
                    
                    data = [{ "fromMailId" : fromMailId, "date" : date, "currentStatus" : currentStatus, "arrivesOn" : arrivesOn, "orderNumber" : orderNumber, "orderItemsList": orderItemsList,"vendor" : vendor, "grandTotal" : grandTotal, "deliveryAddress" : deliveryAddress,}]
                    print(data)
                    return json.dumps(data)
    else:
        data = "No Data"
        print(data) 
        return json.dumps(data)


