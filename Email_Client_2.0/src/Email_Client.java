
//180594V
import java.io.*;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email_Client {
	private static ArrayList<Reciepient> reciepients;
	private static ArrayList<IBday> birthdayknows;
	
    public static void main(String[] args) {
    	Queue queue=new Queue(5);
         EmailReciever reciever=new EmailReciever("pop.gmail.com", "pop3","techbois98@gmail.com", "pubg1234",queue);
         EmailSerializer serializer=new EmailSerializer(queue);
         EmailStatPrinter printer=new EmailStatPrinter();
         EmailStatRecorder recorder=new EmailStatRecorder();
         reciever.addObservers(recorder);
         reciever.addObservers(printer);
         reciever.start();
         serializer.start();
    	reciepients=new ArrayList<Reciepient>();
    	birthdayknows=new ArrayList<IBday>();
    	try (BufferedReader buffer_reader=new BufferedReader(new FileReader("clientList.txt"));) {	
  			String details=buffer_reader.readLine();
  			while(details!=null) {
  				Reciepient r=ApplicationOperation.makeReciepientObjects(details);
  				reciepients.add(r);
  				if (ApplicationOperation.isBirthdayknown(r)!=null){birthdayknows.add(ApplicationOperation.isBirthdayknown(r));}
  				details=buffer_reader.readLine();	
  			}
  		}catch(IOException e) {
  			System.out.println("IO Exception Occured");
  		}
    	ApplicationOperation.automaticWish(birthdayknows);
    		

            Scanner scanner = new Scanner(System.in);
            boolean condition=true;
 while(condition) {           
            System.out.println("Enter option type: \n"
                  + "1 - Adding a new recipient\n"
                  + "2 - Sending an email\n"
                  + "3 - Printing out all the recipients who have birthdays\n"
                  + "4 - Printing out details of all the emails sent\n"
                  + "5 - Printing out the number of recipient objects in the application\n"
                  +"-1 - Exit Application");
      try {
            int option = scanner.nextInt();

            switch(option){
                  case 1:
                	  String detail=scanner.nextLine().trim();
                	  ApplicationOperation.writeToClientList(detail);
                	  Reciepient r=ApplicationOperation.makeReciepientObjects(detail);
                	  
                	  if(r!=null) {
                		  reciepients.add(r);
                		  if (ApplicationOperation.isBirthdayknown(r)!=null){
                			  birthdayknows.add(ApplicationOperation.isBirthdayknown(r));
                			  ArrayList<IBday> added=new ArrayList<IBday>();
                			  added.add((IBday) r);
                			  ApplicationOperation.automaticWish(added);
                			  }
                	  }else {
                		  System.out.println("invalid input");
                	  }
                	  
                	  
                      // input format - Official: nimal,nimal@gmail.com,ceo
                      // Use a single input to get all the details of a recipient
                      // code to add a new recipient
                      // store details in clientList.txt file
                      // Hint: use methods for reading and writing files
                      break;
                  case 2:
                	  String[] detail_arr=scanner.nextLine().trim().split(",");
                	  String email=detail_arr[0];
                	  String subject =detail_arr[1];
                	  String content=detail_arr[2];
                	  SimpleDateFormat f=new SimpleDateFormat("yyyy/MM/dd");
                	  Date date=new Date();
                	  String today = f.format(date);
                	  Email e=new Email(email, subject, content,today);
                	  EmailSerialization.Serialization(e);
                	  EmailviaTLS.sendEmail(e);
                	  System.out.println("mail sent to "+e.getEmail_address());
                      // input format - email, subject, content
                      // code to send an email
                      break;
                  case 3:
                	  String date1=scanner.nextLine().trim();
                	  boolean state=true;
                	  for (IBday i :birthdayknows) {
                		  if (i instanceof PersonalReciepient) {
                			  if (date1.equals(((PersonalReciepient) i).getBirthday())) {
                				  state=false;
                				  System.out.println(((PersonalReciepient) i).getName());}
                		  }else if(i instanceof OfficialFriend) {
                			  if(date1.equals(((OfficialFriend) i).getBirthdday())) {
                				  state=false;
                				  System.out.println(((OfficialFriend) i).getName());}
                		  }
                		  }
                	 if (state) {System.out.println("Nobody has a birthday on that day");}
                		 
                		
                	  
                      // input format - yyyy/MM/dd (ex: 2018/09/17)
                      // code to print recipients who have birthdays on the given date
                      break;
                  case 4:
                	  String date_mail=scanner.nextLine().trim();
                	  ArrayList<Email> mails=EmailSerialization.Deserialization();
                	  boolean state1=true;
                	  if (mails!=null) {
                	  for (Email m:mails) {
                		  if(m.getDate().contentEquals(date_mail)) {
                			  System.out.println("Email sent to: "+m.getEmail_address());
                			  System.out.println("Email subject: "+m.getSubject());
                			  System.out.println("Email Content:"+m.getContent());
                			  System.out.println(" ");
                			  state1=false;
                		  }
                	  }
                	  }
                	  if(state1) {System.out.println("No Emails sent on that day");}
                      // input format - yyyy/MM/dd (ex: 2018/09/17)
                      // code to print the details of all the mails sent on the input date
                      break;
                  case 5:
                      System.out.println("Reciepient count is "+Reciepient.getCount());
                      break;
                      //code to print number of recipients
                  case -1:
                	  condition=false;
                	  break;
                default:
                		  System.out.println("Invalid input");

            }
      }catch(InputMismatchException e) {
    	  System.out.println("Invalid Operation");
    	  scanner.nextLine();
      }
            
 		}
 		scanner.close();
            

        }
}
abstract class ApplicationOperation {
	
	
	
	public static Reciepient makeReciepientObjects(String details) {
		String[] arr=details.split(":");
		String type=arr[0];
		String[] others=arr[1].trim().split(",");
		if(type.equals("Official")) {
			OfficialReciepient office=new OfficialReciepient(others[0], others[1], others[2]);
			return office;
		}
		else if (type.equals("Office_friend")) {
			OfficialFriend friend=new OfficialFriend(others[0], others[1], others[2], others[3]);
			return friend;
		}
		else if (type.equals("Personal")) {
			PersonalReciepient personal=new PersonalReciepient(others[0], others[1], others[2], others[3]);
			return personal;
		}
		return null;
		
	}
	public static void writeToClientList(String details) {
		try(FileWriter file_writer=new FileWriter("clientList.txt",true)){
			file_writer.write(details+"\n");
			
		} catch (IOException e) {
			
			System.out.println("Error "+e);
		}
	}
	public static IBday isBirthdayknown(Reciepient r) {
		if (r instanceof PersonalReciepient) {
			return (PersonalReciepient) r;
		}
		else if (r instanceof OfficialFriend) {
			return (OfficialFriend) r;
		}
		return null;
	}
	
	public static void automaticWish(ArrayList<IBday> birthdayknows) {
		SimpleDateFormat f=new SimpleDateFormat("MM/dd");
		SimpleDateFormat f_year=new SimpleDateFormat("yyyy/MM/dd");
		Date date=new Date();
		String today = f.format(date);
		String today_with_year=f_year.format(date);
		String birthday=null;
		String mail;
		for (IBday i: birthdayknows) {
			if(i instanceof PersonalReciepient) {
				birthday=((PersonalReciepient) i).getBirthday();
				mail=((PersonalReciepient) i).getEmail();
			}
			else {
				birthday=((OfficialFriend) i).getBirthdday();
				mail=((OfficialFriend) i).getEmail();
			}
			
			String[] bday_arr=birthday.trim().split("/");
			String current_bday=bday_arr[1]+"/"+bday_arr[2];
			if(today.equals(current_bday)) {
				Email newMail=new Email(mail,"Happy Birthday",i.sendWishes(),today_with_year);
				EmailSerialization.Serialization(newMail);
				EmailviaTLS.sendEmail(newMail);
				
			}
		}
		
	}

}
class Email implements Serializable {
	
	private static final long serialVersionUID = 7367431175983387721L;
	private String email_address;
	private String subject;
	private String content;
	private String date;
	public Email(String email_dress, String subject, String content,String date) {
		super();
		this.email_address = email_dress;
		this.subject = subject;
		this.content = content;
		this.date=date;
	}
	public String getDate() {
		return date;
	}
	public String getEmail_address() {
		return email_address;
	}
	public String getSubject() {
		return subject;
	}
	public String getContent() {
		return content;
	}
	

}
abstract class EmailSerialization {
	
	
	public static ArrayList<Email> Deserialization() {
		try(FileInputStream file = new FileInputStream("Email Objects.txt");ObjectInputStream in = new ObjectInputStream(file);){
			@SuppressWarnings("unchecked")
			ArrayList<Email> mails=(ArrayList<Email>) in.readObject();
			return mails;
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found");
		}
		return null;
	}
	
	public static void Serialization(Email e) {
		ArrayList<Email> mails=Deserialization();
		if(mails==null) {mails=new ArrayList<Email>();}
		try (FileOutputStream file = new FileOutputStream("Email Objects.txt"); ObjectOutputStream out = new ObjectOutputStream(file);){
			mails.add(e);
			out.writeObject(mails);
			
		} catch (FileNotFoundException e1) {
			System.out.println("File Not Found Exception");
		} catch (IOException e1) {
			System.out.println("IOEXception");
		}
	}
	

}

abstract class EmailviaTLS {
	
	public static String username = "techbois98@gmail.com";
    public static String password = "pubg1234";
    
    
    
	public static  void sendEmail(Email em) {

        
        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(em.getEmail_address())
            );
            message.setSubject(em.getSubject());
            message.setText(em.getContent());

            Transport.send(message);



        } catch (MessagingException e) {
            e.printStackTrace();
        }
}


}
interface IBday {
	public String sendWishes();

}
class OfficialFriend extends OfficialReciepient implements IBday{
	private String Birthdday;

	public OfficialFriend(String name, String email, String desiganation, String birthdday) {
		super(name, email, desiganation);
		Birthdday = birthdday;
	}

	public String getBirthdday() {
		return Birthdday;
	}

	@Override
	public String sendWishes() {
		String content="Wish you a Happy Birthday. from Avishka";
		return content;
		
	}
	
	

}
class OfficialReciepient extends Reciepient{
	private String desiganation;

	public OfficialReciepient(String name, String email, String desiganation) {
		super(name, email);
		this.desiganation = desiganation;
	}

	public String getDesiganation() {
		return desiganation;
	}
	

}
class PersonalReciepient extends Reciepient implements IBday{
	private String nickname;
	private String Birthday;
	public PersonalReciepient(String name,  String nickname, String email,String birthday) {
		super(name, email);
		this.nickname = nickname;
		Birthday = birthday;
	}
	public String getNickname() {
		return nickname;
	}
	public String getBirthday() {
		return Birthday;
	}
	@Override
	public String sendWishes() {
		String content="Hugs and love on your birthday. from Avishka";
		return content;
	}

}
abstract class Reciepient {
	private static int count;
	public static int getCount() {
		return count;
	}
	private String name;
	private String email;
	public Reciepient(String name, String email) {
		super();
		this.name = name;
		this.email = email;
		Reciepient.count++;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	

}








class EmailReciever extends Thread {
	private ArrayList<Observer> observers;
	private Queue queue;
	private int count;
	private String hostval;
	private String mailStrProt;
	private String username;
	private String password;
	
	EmailReciever(String hostval,String mailStrProt,String username,String password,Queue queue){
		observers=new ArrayList<>();
		this.queue=queue;
		this.hostval=hostval;
		this.mailStrProt=mailStrProt;
		this.username=username;
		this.password=password;
		this.count=0;
	}
	private void RecieveMail() 
	   {
	      try {
	    	
	      Properties propvals = new Properties();
	      propvals.put("mail.pop3.host", hostval);
	      propvals.put("mail.pop3.port", "995");
	      propvals.put("mail.pop3.starttls.enable", "true");
	      Session emailSessionObj = Session.getDefaultInstance(propvals);  
	      
	      Store storeObj = emailSessionObj.getStore("pop3s");
	      storeObj.connect(hostval, username, password);
	      
	      Folder emailFolderObj = storeObj.getFolder("INBOX");
	      emailFolderObj.open(Folder.READ_ONLY);
	      
	      Message[] messageobjs = emailFolderObj.getMessages(); 
	 
	      for (int i = count, n = messageobjs.length; i < n; i++) {
	         Message indvidualmsg = messageobjs[i];
	         Email email=new Email(indvidualmsg.getFrom()[0].toString(),indvidualmsg.getSubject(),indvidualmsg.getContent().toString(),indvidualmsg.getSentDate().toString());
	         queue.enqueue(email);
	         notifyObservers(email);
	         count++;
	         
	 
	      }
	      
	      emailFolderObj.close(false);
	      storeObj.close();
	      } catch (NoSuchProviderException exp) {
	         exp.printStackTrace();
	      } catch (MessagingException exp) {
	         exp.printStackTrace();
	      } catch (Exception exp) {
	         exp.printStackTrace();
	      }
	   }
	
	private void notifyObservers(Email email) {
		for (int j=0;j<observers.size();j++) {
			observers.get(j).update(email);
		}
		
	}
	public void addObservers(Observer observer) {
		observers.add(observer);
	}
	public void removeObservers(Observer observer) {
		observers.remove(observer);
		return;
	}
	public void run() {
		while (true) {
			RecieveMail();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}

}

class EmailStatPrinter implements Observer{
	private void writetoFile(String s) {
		
		File file = new File("RecievedMails.txt");
		FileWriter fr;
		try {
			fr = new FileWriter(file, true);
			BufferedWriter br = new BufferedWriter(fr);
			br.write(s+"\n");

			br.close();
			fr.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void update(Email email) {
		String s="an email is received at "+email.getDate();
		writetoFile(s);
		
	}
}
class EmailStatRecorder implements Observer {

		@Override
		public void update(Email email) {
			String s="an email is received at "+email.getDate();
			System.out.println(s);
			
			
		}
		
		

	}
interface Observer {
	public void update(Email email);

}
class EmailSerializer extends Thread{
	private int count;
	private Queue queue;
	public EmailSerializer(Queue queue) {
		this.count=0;
		this.queue=queue;
	}
	public void Serializemail() {
		Email mail=queue.dequeue();
		try {
			String name="Mail "+count+".txt";
			
	         FileOutputStream fileOut =new FileOutputStream(name);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(mail);
	         out.close();
	         fileOut.close();

	      } catch (IOException i) {
	         i.printStackTrace();
	      }
		count++;
		
	}
	public void run() {
		while(true) {
			Serializemail();
		}
	}

}


class Queue {
	private LinkedList<Email> queue;
	private int size;
	private int count;
	Queue(int size){
		this.size=size;
		this.queue=new LinkedList<>();
		count=0;
	}
	public synchronized void enqueue(Email email) {
		while(count==size) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		queue.add(email);
		count++;
		notifyAll();
	}
	public synchronized Email dequeue() {
		while(count==0) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		Email mail=queue.remove(0);
		count--;
		notifyAll();
		return mail;
	}

}







