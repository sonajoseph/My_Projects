##################         TO JAVA LAZADA       #############
from __future__ import print_function
from __future__ import unicode_literals
import base64
import email
import json
import re

SCOPES = ' https://mail.google.com/auth/gmail.readonly'

def lazada(d, sub, fm, a):
    global currentStatus

    print(d)
    print(sub)
    print(fm)
    print(a)
    date_x = d
    subjct = sub
    frm_id = str(fm)
    message = str(a)
    vendor = 'Lazada'
    mail_list = ['Lazada Singapore <orders@orders.lazada.sg>']
    if frm_id in mail_list:
        try:
            date = date_x                
        except:
            date = 'Date is not available!!!'
        
        try:
            sub_msg_1 = subjct
            rgx_5 = re.findall('Order Confirmation',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Ordered'

            rgx_5 = re.findall('has been shipped',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Shipped'
            rgx_5 = re.findall('has been Delivered',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Delivered'
                fromMailId = frm_id.split('<')[1].split('>')[0]
                orderNumber = subjct.split('#')[1].split(' ')[0].replace(')','').replace(' ','').replace("'",'').replace('.','')
                data = [{"fromMailId" : fromMailId, "date" : date,"currentStatus" : currentStatus,"orderNumber" : orderNumber,"vendor" : vendor, }]
                return json.dumps(data)
            rgx_5 = re.findall('have been cancelled',sub_msg_1)
            if (len(rgx_5)>0):
                currentStatus = 'Cancelled'
                fromMailId = frm_id.split('<')[1].split('>')[0]
                orderNumber = subjct.split('#')[1].split(' ')[0].replace(')','').replace(' ','').replace("'",'').replace('.','')
                data = [{"fromMailId" : fromMailId, "date" : date,"currentStatus" : currentStatus,"orderNumber" : orderNumber,"vendor" : vendor, }]
                return json.dumps(data)
        except:
            currentStatus = 'Status not available!!!'
        
        try:
            fromMailId = frm_id.split('<')[1].split('>')[0]                 
        except:
            fromMailId = 'Mail From is not available!!!'
        
        mailsep = re.findall('Lazada Singapore <orders@orders.lazada.sg>',frm_id)


        if (len(mailsep)>0):
            msg_str = base64.urlsafe_b64decode(message.encode('UTF8'))
            message_111 = str(email.message_from_string(msg_str)).decode('UTF8').replace('>','')
            rgx_2 = re.compile('json"\r\n\t')
            final_msg_2 = rgx_2.split(message_111)[1].split('\r\n\t</script')[0].replace('\r\n\t','').replace("'",'"')
            final_msg = json.loads(final_msg_2)
            orderNumber = final_msg['orderNumber']
            
            no_of_prdcts = len(final_msg['acceptedOffer'])
            orderItemsList = []
            
            for i in range(0,no_of_prdcts):
                qty = final_msg['acceptedOffer'][i]['eligibleQuantity']['value']
                item = str(final_msg['acceptedOffer'][i]['itemOffered']['name']).replace(",","").replace("quot","").replace('-'," ").replace('&','').replace('&amp;','').replace(';',"").replace('amp','and')
                subTotal = 'SGD ' + final_msg['acceptedOffer'][i]['price']
                mult_prdct = {"item" : item,"qty": qty, "subTotal" : subTotal }
                orderItemsList.append(mult_prdct)                            
                
            grandTotal = 'SGD '+ final_msg['price']
            date = final_msg['orderDate']
            deliveryAddress = final_msg['billingAddress']['addressCountry'] + final_msg['billingAddress']['name'] + final_msg['billingAddress']['addressLocality'] + final_msg['billingAddress']['addressRegion']    
            shipment_arraival_1 = message_111.split('Shipment will arrive by:')[1].split('</p')[0]
            try:
                shipment_arraival_2 = shipment_arraival_1.split('class="')[1].split('</span')[0]
                arrivesOn = shipment_arraival_2.split('"')[1]
            except:
                arrivesOn = shipment_arraival_1.split('</b')[1]

	        data = [{ "fromMailId" : fromMailId, "date" : date, "currentStatus" : currentStatus, "arrivesOn" : arrivesOn, "orderNumber" : orderNumber, "orderItemsList":orderItemsList ,"vendor" : vendor, "grandTotal" : grandTotal, "deliveryAddress" : deliveryAddress}]
	        return json.dumps(data)
    else:
        data = "No Data"
        return json.dumps(data)
