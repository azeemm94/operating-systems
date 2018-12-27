//C:\Azeem\SMU\7343 Operating Systems and System Software\Project\sample_input.txt
//C:\Azeem\SMU\7343 Operating Systems and System Software\Project\azeem_testcase.txt
import java.util.*;
import java.io.*;
import java.nio.file.Files;
class PCB 
{
    int pid,arrival,burst,priority; 
    public PCB(String pid, String arrival, String burst, String priority) 
    {  
        this.pid = Integer.parseInt(pid);
        this.arrival = Integer.parseInt(arrival);  
        this.burst = Integer.parseInt(burst);  
        this.priority = Integer.parseInt(priority);  
    }   
}
class PCBM
{
    int pid,arrival,duration,size;
    public PCBM(String pid, String arrival, String duration, String size)
    {
        this.pid = Integer.parseInt(pid);
        this.arrival = Integer.parseInt(arrival);  
        this.duration = Integer.parseInt(duration);  
        this.size = Integer.parseInt(size); 
    }
}
public class Scheduler{
    public static boolean isNumeric(String s) 
    {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }  
    public static void printQueue(LinkedList<PCB> processes)
    {
        int i=0; int n=processes.size();
        System.out.print("Processes in queue: ");
        if(n>0)
            for(PCB p:processes)
            {
                if(i != n-1)
                    System.out.print(p.pid+" -> ");
                else
                    System.out.print(p.pid+"\n");
                i++;
            }
        else
            System.out.print("Queue empty\n");
        System.out.println();
    }
    public static void FCFS(LinkedList<PCB> processes)
    {
        System.out.println("\n***********FCFS***********");
        int minarrival=100000;
        int n=processes.size();
        float turnaround=0;
        float waittime=0;
        int currtime=0;
        int priority=5;
        int currburst=0;
        boolean found=true,current=false;
        LinkedList<PCB> ready= new LinkedList<PCB>();
        PCB running=processes.getFirst();
        PCB earliest=processes.getFirst();
        String gantt="\nFCFS Gantt Chart\n0";
        int totaltime=0;
        for(PCB p:processes)
        {
            totaltime+=p.burst;
        }
        for(currtime=0;currtime<=totaltime;currtime++)
        {
            found=true;
            if(processes.size()>0)
            {
                while(found)
                {
                    found=false;
                    for(PCB p:processes)
                    {
                        if(p.arrival==currtime && p.priority<priority)
                        {
                            earliest=p;
                            priority=p.priority;
                            found=true;
                        }
                    }  
                    if(found)
                    {
                        ready.add(earliest);
                        System.out.println("Process P"+earliest.pid+" added at "+ currtime+"ms");
                        printQueue(ready);
                        processes.remove(earliest);
                        priority=5;
                    }
                }
            }
            if(current&&currburst==currtime&&currtime>0)
            {
                System.out.println("Process P"+running.pid+" completed at "+currtime+"ms");
                ready.remove(running);
                turnaround+=currtime-running.arrival;
                waittime+=currtime-running.arrival-running.burst;
                current=false;
                gantt+=" - P"+running.pid+" - "+(currtime);
                printQueue(ready);
                if(currtime==totaltime) break;
            }
            if(!current)
            {
                running=ready.getFirst();
                currburst+=running.burst;
                current=true;
            }
        }
        turnaround/=n;        
        waittime/=n;
        System.out.println(gantt);
        System.out.println("\nFCFS Avg turnaround time="+turnaround);
        System.out.println("\nFCFS Avg waiting time="+waittime);
    }
    public static void SJF(LinkedList<PCB> processes)
    {
        System.out.println("\n***********SJF************");
        int totaltime=0;
        int n=processes.size();
        for(PCB p:processes)
        {
            totaltime+=p.burst;
        }
        LinkedList<PCB> ready=new LinkedList<PCB>();
        PCB addp=processes.getFirst();
        int runningindex=0;
        int shortest=100000;
        int priority=5;
        int currentid=0;
        boolean found=true;
        float turnaround=0, waittime=0;
        int addindex=0;
        String gantt="\nSJF Gantt Chart\n";
        for(int i=0; i<totaltime; i++)
        {
            if(processes.size()>0)
            {
                found=true;
                while(found && processes.size()>0)
                {
                    priority=5;
                    found=false;
                    for(PCB p:processes)
                    {
                        if(p.arrival<=i && p.priority<priority)
                        {
                            addp=p;
                            found=true;
                            priority=p.priority;
                        }
                    }
                    if(found)
                    {
                        addindex=0;
                        for(PCB p:ready)
                        {
                            if(p.burst>addp.burst)
                                break;
                            addindex++;
                        }
                        ready.add(addindex,addp);
                        processes.remove(addp);
                        System.out.println("Process P"+addp.pid+" added at "+i+"ms");
                        printQueue(ready);
                    } 
                }
            }
            PCB current=ready.getFirst();
            if(currentid!=current.pid)
            {
               gantt+=i+" - P"+current.pid+" - ";
               currentid=current.pid;
            }
            current.burst--;
            if(current.burst==0)
            {
                ready.remove(0);
                System.out.println("Process P"+current.pid+" completed at "+(i+1)+"ms");
                turnaround+=i+1-current.arrival;
                printQueue(ready);
            }  
            else
            {
                ready.set(0,current);
            }
        }
        gantt+=totaltime;
        waittime=turnaround-totaltime;
        turnaround/=n;
        waittime/=n;
        System.out.println(gantt);
        System.out.println("\nSJF Avg turnaround time="+turnaround);
        System.out.println("\nSJF Avg waiting time="+waittime);
    }
    
    public static void Priority(LinkedList<PCB> processes)
    {
        System.out.println("\n************Priority************");
        String gantt="\nPriority Gantt Chart\n";
        LinkedList<PCB> ready=new LinkedList<PCB>();
        int total=0,i=0,priority=5,x=-1,n=processes.size();
        float waittime=0,turnaround=0;
        PCB running=processes.get(0);
        boolean added=false;
        for(PCB p:processes)
        {
            total+=p.burst;
        }
        while(i<total)
        {
            added=false;
            priority=5;
            x=-1;
            for(PCB p:processes)
            {
                if(p.arrival<=i)
                {
                    ready.add(p);
                    System.out.println("Process P"+p.pid+" added at "+p.arrival+"ms");
                    printQueue(ready);
                    added=true;
                }
            }
            if(added)
            {
                for(PCB p:ready)
                {
                    processes.remove(p);
                }
            }
            for(PCB p:ready)
            {
                if(p.priority<priority)
                {
                    priority=p.priority;
                    running=p;
                }
            }
            gantt+=i+" - P"+running.pid+" - ";
            i+=running.burst;
            turnaround+=i-running.arrival;
            waittime+=i-running.arrival-running.burst;
            ready.remove(running);
            System.out.println("Process P"+running.pid+" removed at "+i+"ms");
            printQueue(ready);
        }
        gantt+=i;
        waittime/=n;
        turnaround/=n;
        System.out.println(gantt);
        System.out.println("\nPriority Avg turnaround time="+ turnaround);
        System.out.println("\nPriority Avg waiting time="+ waittime);
    }
    
     public static void RR(LinkedList<PCB> processes, int q)
     {
        System.out.println("\n***************RR***************");
        int currtime=0,totaltime=0,n=processes.size(), priority=5, qcount=0;
        float turnaround=0,waittime=0;
        String gantt="\nRR Gantt Chart\n0";
        LinkedList<PCB> ready=new LinkedList<PCB>();
        PCB x=processes.getFirst();
        for(PCB p:processes)
        {
            totaltime+=p.burst;
            waittime+=p.burst;
        }
        while(currtime<=totaltime)
        {
            boolean found=true;
            while(found && processes.size()>0)
            {
                found=false;
                priority=5;
                for(PCB p:processes)
                {
                    if(p.arrival<=currtime&&p.priority<priority)
                    {
                        x=p;
                        priority=p.priority;
                        found=true;
                    }
                }
                if(found)
                {
                    processes.remove(x);
                    ready.add(x);
                    System.out.println("Process P"+x.pid+" added at "+currtime+"ms");
                    printQueue(ready);
                }
            }
            if(currtime>=1)
            {
                PCB curr=ready.getFirst();
                curr.burst--;
                qcount++;
                if(curr.burst==0)
                {
                    ready.remove();
                    qcount=0;
                    System.out.println("Process P"+curr.pid+" completed at "+currtime+"ms");
                    printQueue(ready);
                    gantt+=" - P"+curr.pid+" - "+currtime;
                    turnaround+=currtime-curr.arrival;
                }
                else if(qcount==q)
                {
                    ready.remove();
                    ready.add(curr);
                    qcount=0;
                    gantt+=" - P"+curr.pid+" - "+currtime;
                    System.out.println("Process P"+curr.pid+" quantum expired at "+currtime+"ms");
                    printQueue(ready);
                }
                else
                {
                    ready.set(0,curr);
                }
            }
            currtime++;
        }
        waittime=(turnaround-waittime)/n;
        turnaround/=n;
        System.out.println(gantt);
        System.out.println("\nRR Avg turnaround time="+turnaround);
        System.out.println("\nRR Avg waiting time="+waittime);
    }
     
    public static void FirstFit(LinkedList<PCBM> processlist, int holes[][],int processn,int holesn,int mem)
    {
        System.out.println("Algorithm: First Fit");
        String allocated="",blocked="";
        int blockedn=0,alloc=0;
        for(int i=0;i<holesn;i++)
        {
            alloc+=holes[i][1];
        }
        alloc=mem-alloc;
        for(PCBM p:processlist)
        {
            for(int i=0;i<holesn;i++)
            {
                if(holes[i][1]>p.size)
                {
                    holes[i][1]-=p.size;
                    System.out.println("Process P"+p.pid+ " added in hole "+(i+1)); 
                    allocated+="P"+p.pid+", "+holes[i][0]+", "+p.size+"; ";
                    holes[i][0]+=p.size;
                    alloc+=p.size;
                    break;
                }
                if(i==holesn-1)
                {
                    blocked+="P"+p.pid+", ";
                    blockedn++;
                }
            }
        }
        System.out.println("Memory allocated processes : "+allocated);
        System.out.println("Blocked processes: "+(blockedn>0?blocked:"None"));
        System.out.println("Memory Utilization: " +alloc+"/"+mem+" = "+((float)alloc/mem*100)+"%");
        System.out.println("Blocking Probability: "+ blockedn+"/"+processn+" = "+((float)blockedn/processn*100)+"%");
    }
     
    public static void WorstFit(LinkedList<PCBM> processlist, int holes[][],int processn,int holesn,int mem)
    {
        System.out.println("Algorithm: Worst Fit");
        String allocated="",blocked="";
        int blockedn=0,alloc=0,worsthole=-1,worstindex=-1;
        for(int i=0;i<holesn;i++)
        {
            alloc+=holes[i][1];
        }
        alloc=mem-alloc;
        for(PCBM p:processlist)
        {
            worsthole=-1;
            worstindex=-1;
            for(int i=0;i<holesn;i++)
            {
                if(holes[i][1]>worsthole) 
                {
                    worsthole=holes[i][1];
                    worstindex=i;
                }
            }
            if(p.size<worsthole)
            {
                System.out.println("Process P"+p.pid+" added in hole "+(worstindex+1));
                allocated+="P"+p.pid+", "+holes[worstindex][0]+", "+p.size+"; ";
                holes[worstindex][0]+=p.size;
                holes[worstindex][1]-=p.size;
                alloc+=p.size;
            }
            else
            {
                blockedn++;
                blocked+="P"+p.pid+"; ";
            }
        }
        System.out.println("Memory allocated processes : "+allocated);
        System.out.println("Blocked processes: "+(blockedn>0?blocked:"None"));
        System.out.println("Memory Utilization: " +alloc+"/"+mem+" = "+((float)alloc/mem*100)+"%");
        System.out.println("Blocking Probability: "+ blockedn+"/"+processn+" = "+((float)blockedn/processn*100)+"%");
    }
     
    public static void BestFit(LinkedList<PCBM> processlist, int holes[][],int processn,int holesn,int mem)
    {
        System.out.println("Algorithm: Best Fit");
        String allocated="",blocked="";
        int blockedn=0,alloc=0,besthole=100000,bestindex=-1;
        boolean flag=false;
        for(int i=0;i<holesn;i++)
        {
            alloc+=holes[i][1];
        }
        alloc=mem-alloc;
        for(PCBM p:processlist)
        {
            flag=false;
            besthole=100000; 
            bestindex=-1;
            for(int i=0;i<holesn;i++)
            {
                if(holes[i][1]>=p.size && holes[i][1]<besthole)
                {
                    besthole=holes[i][1];
                    bestindex=i;
                    flag=true;
                }
            }
            if(flag)
            {
                System.out.println("Process P"+p.pid+" added to hole "+(bestindex+1));
                allocated+="P"+p.pid+", "+holes[bestindex][0]+", "+p.size+"; ";
                holes[bestindex][0]+=p.size;
                holes[bestindex][1]-=p.size;
                alloc+=p.size;
            }
            else
            {
                blockedn++;
                blocked+="P"+p.pid+"; ";
            }
        }
        System.out.println("Memory allocated processes : "+allocated);
        System.out.println("Blocked processes: "+(blockedn>0?blocked:"None"));
        System.out.println("Memory Utilization: " +alloc+"/"+mem+" = "+((float)alloc/mem*100)+"%");
        System.out.println("Blocking Probability: "+ blockedn+"/"+processn+" = "+((float)blockedn/processn*100)+"%");
    }
     
    public static void main(String args[]) throws IOException
    {
        LinkedList<PCB> processes=new LinkedList<PCB>();  
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter 1.Process Scheduler or 2.Memory management");
        String option=sc.nextLine();
        while(true)
        {
            if(option.equals("1"))
            {
                boolean choicewrong=true;
                String choice="";
                while(choicewrong)
                {
                    System.out.println("How do you want to input the PCB data? 1. Manual Entry 2. Text File");
                    choice=sc.nextLine();
                    if(choice.equals("1")||choice.equals("2"))
                    {
                        choicewrong=false;
                    }
                    else
                    {
                        System.out.println("Please enter only number 1 or 2");
                    }
                }
                if(choice.equals("1"))
                {
                    String process="";
                    int pnum=1;
                    boolean error=false;
                    while(!process.equals("x"))
                    {
                        if(!error)
                        System.out.println("Enter details of the next process or enter x to exit");
                        error=false;
                        process=sc.nextLine();
                        process=process.toLowerCase();
                        if(!process.equals("x"))
                        {
                            String processinfo[]=process.split(",");
                            for(int i=0;i<processinfo.length;i++)
                            {
                                processinfo[i]=processinfo[i].trim();
                                if(!isNumeric(processinfo[i]))
                                    error=true;
                            }
                            if(processinfo.length!=4)
                                error=true;
                            if(error)
                            {
                                System.out.println("The last process you entered is invalid. Enter process details again or enter x to exit");
                            }
                            else
                            {
                                PCB x=new PCB(processinfo[0],processinfo[1],processinfo[2],processinfo[3]);
                                processes.add(x);
                                System.out.println("Process P"+pnum+" added");
                                pnum++;
                            }
                        }   
                    }
                }
                else if(choice.equals("2"))
                {
                    System.out.println("Enter the txt file path");
                    boolean error=true;
                    boolean fileerror=false;
                    int pnum=1;
                    while(error)
                    {
                        error=false;
                        String filepath=sc.nextLine();
                        File txtfile= new File(filepath);
                        if(!txtfile.exists())
                        {
                            System.out.println("File does not exist, please enter the file path again");
                            error=true;
                        }
                        else
                        {
                            Scanner fr=new Scanner(txtfile);

                            while(fr.hasNextLine())
                            {
                                fileerror=false;
                                String process=fr.nextLine();
                                String[] processinfo=process.split(",");
                                for(int i=0;i<processinfo.length;i++)
                                {
                                    processinfo[i]=processinfo[i].trim();
                                    if(!isNumeric(processinfo[i]))
                                        fileerror=true;
                                }
                                if(processinfo.length!=4)
                                    fileerror=true;
                                if(!fileerror)
                                {
                                    PCB x=new PCB(processinfo[0],processinfo[1],processinfo[2],processinfo[3]);
                                    processes.add(x);
                                    System.out.println("Process P"+processinfo[0]+" added"+" ["+processinfo[0]+","+processinfo[1]+","+processinfo[2]+","+processinfo[3]+"]");
                                    pnum++;
                                }
                                else
                                {
                                    System.out.println("The inputs in this file are not correct, please try another file or make changes");
                                    error=true;
                                }
                            }
                        }
                    }
                }

                String del="";
                while(processes.size()>0&&!del.equals("n"))
                {
                    System.out.println("Do you want to delete any PCB? Enter PCB PID or enter N");
                    del= sc.nextLine();
                    del=del.toLowerCase();
                    boolean delete=false;
                    PCB x=processes.get(0);
                    if(!del.equals("n"))
                    {
                        if(isNumeric(del))
                        {
                            for(PCB p:processes)
                            {
                                if(Integer.parseInt(del)==p.pid)
                                {
                                    x=p;
                                    delete=true;
                                }
                            }
                            if(delete)
                            {
                                processes.remove(x);
                            }
                        }
                        else
                        {
                            System.out.println("Your input is not valid");
                        }
                    }
                }

                boolean qerror=true;
                System.out.println("Enter the time quantum for Round Robin: ");
                String q="";
                while(qerror)
                {
                    q=sc.next();
                    if(isNumeric(q))
                    {
                        qerror=false;
                    }
                    else
                    {
                        System.out.println("Your input is invalid, please enter numbers only");
                    }
                }


                LinkedList<PCB> LLFCFS= new LinkedList<PCB>();
                LLFCFS=(LinkedList)processes.clone();

                LinkedList<PCB> LLSJF= new LinkedList<PCB>();
                LLSJF=(LinkedList)processes.clone();

                LinkedList<PCB> LLRR= new LinkedList<PCB>();
                LLRR=(LinkedList)processes.clone();

                LinkedList<PCB> LLPri= new LinkedList<PCB>();
                LLPri=(LinkedList)processes.clone();

                if(processes.size()>0)
                {
                    FCFS(LLFCFS);        
                    Priority(LLPri);
                    //RR(LLRR,Integer.parseInt(q));
                    SJF(LLSJF);
                }
                else
                {
                    System.out.println("Please try again, all your processes were deleted");
                }
                break;
            }
            else if(option.equals("2"))
            {
                //Memory management
                int holes[][]=new int[100][2];
                int memsize=-1,spaces=-1,process=-1;
                LinkedList<PCBM> processlist=new LinkedList<PCBM>();
                System.out.println("Enter the path for the txt input file");
                String filepath="";
                while(true)
                {
                    filepath=sc.nextLine();
                    File txtfile= new File(filepath);
                    if(!txtfile.exists())
                    {
                        System.out.println("File does not exist, please enter the file path again");
                    }
                    else break;
                }
                File txtfile=new File(filepath);
                Scanner fr=new Scanner(txtfile);
                while(fr.hasNextLine())
                {
                    if(memsize<0)
                    {
                        memsize=Integer.parseInt(fr.nextLine().trim());
                    }
                    else if(spaces<0)
                    {
                        spaces=Integer.parseInt(fr.nextLine().trim());
                        for(int i=0;i<spaces;i++)
                        {
                            String spacetext=fr.nextLine();
                            int stradd=Integer.parseInt(spacetext.substring(0,spacetext.indexOf(",")).trim());
                            holes[i][0]=stradd;
                            int avladd=Integer.parseInt(spacetext.substring(spacetext.indexOf(",")+1).trim());
                            holes[i][1]=avladd;
                        }
                    }
                    else if(process<0)
                    {
                        process=Integer.parseInt(fr.nextLine().trim());
                        for(int i=0;i<process;i++)
                        {
                            String processinfo[]=fr.nextLine().split(",");
                            for(int j=0;j<processinfo.length;j++)
                            {
                                processinfo[j]=processinfo[j].trim();
                            }
                            PCBM x=new PCBM(processinfo[0],processinfo[1],processinfo[2],processinfo[3]);
                            processlist.add(x); 
                        }
                    }
                }
                System.out.println("Enter 1. First Fit 2. Worst Fit 3. Best Fit");
                String fittype=sc.nextLine();
                while(true)
                {
                    if(fittype.equals("1"))
                    {
                        FirstFit(processlist,holes,process,spaces,memsize);
                        break;
                    }
                    else if(fittype.equals("2"))
                    {
                        WorstFit(processlist,holes,process,spaces,memsize);
                        break;
                    }
                    else if(fittype.equals("3"))
                    {
                        BestFit(processlist,holes,process,spaces,memsize);
                        break;
                    }
                    else
                    {
                        System.out.println("Your input is incorrect please try again, 1. First Fit 2. Worst Fit 3. Best Fit");
                        fittype=sc.nextLine();
                    }      
                }
                break;
            }
            else
            {
                System.out.println("Your input was invalid, please try again\n1.Process scheduler 2.Memory Management");
                option=sc.nextLine();
            }
        }     
    }
}