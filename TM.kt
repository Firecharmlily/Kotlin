/*
 * Course: CSE 4250, Spring 2019
 * Project: Proj2, Simulation (TM)
 * Implementation:  Kotlin version 1.1.4-2 (JRE 1.8.0_131-b11)
 */

//a lot of this is my code and i actually put in the effort,
//just I don't want problems with my partner who i have to deal with in another class

package main

import java.io.File
import java.io.*

fun main(args: Array<String>) {
	val fileName = args[0] //file name inputted
    
    val fileList = mutableListOf<String>() //changable list of string

    File(fileName).useLines { lines -> fileList.addAll(lines) } //adds txt files line by line as a list
	
	
	/*reads in terring machine requirements*/
	var firstLine: String = fileList[0] //find amount of commands
	var numberSteps: Int = firstLine.split(" ")[0].toInt() //convert number of commands char to int
	var alphabet = firstLine.split(" ")[1].toString() //decipher alphabet into a string
	
	var commReference = Array(numberSteps, {IntArray(alphabet.length)})//helps track command reference
	var alphabetChange = Array(numberSteps, {CharArray(alphabet.length)})//helps track changing of tape in command
	var description = arrayOfNulls<String>(numberSteps) //command description
	var action = CharArray(numberSteps) //what to do
	
	
	for(i in 0..(numberSteps-1)){
		var currentLine: String = fileList[1+i].replace("\\s+".toRegex(), " ") //splits input strings so they can be read
		
		action[i] = currentLine.split(" ")[1][0] //what to do aka L, R, Y, N
		description[i] = currentLine.substring((2 + alphabet.length*2)*2) //command description
		
		for(j in 0..(alphabet.length-1)){
			commReference[i][j] = currentLine.split(" ")[2+j].toInt() //next command list according to symbols, problem area
		}
		
		for(j in 0..(alphabet.length-1)){
			alphabetChange[i][j] = currentLine.split(" ")[2+alphabet.length+j][0] //indicates what to change the tape too
		}
		
	}
	

	var input = readLine().toString() //reads in tape inputs
	
	var tapes = input.split(" ") //splits the inputted tapes by spaces
	
	for(tape in tapes){
		var flag: Int = 1 //changes when tm reaches an end
		
		//make a tmp of tape that is mutable
		var tmp = tape
		
		//add # to tape
		tmp = alphabet.last() + tmp + alphabet.last()
		
		
		var exec: Int = 0 //which execution the machine is in
		var state: Int = 0 //what state the tm is in
		var toDo: Char = action[state] //action to fulfill
		var position: Int = 0
		
		do{
			//perform action associated with state
			when (toDo){
				'R' -> position = position.inc() //go forward on tape
				'L' -> position = position.dec() //go back on tape
				'Y' -> flag = 0 //good eject
				'N' -> flag = -1 //bad eject
			}
			
			printcommand(exec, state, position, tmp, description[state])
			
			//check and possibly change alphabet and grab next state
			loop@ for (i in alphabet.indices){
				if (tmp[position] == alphabet[i]){
					//change alphabet if necessary
					tmp = tmp.substring(0, position) + alphabetChange[state][i] + tmp.substring(position + 1)
					//grab next state
					state = commReference[state][i]
					
					//breaks loop if alphabet found
					break@loop
				}
				
			}
			
			if((tmp[0] != alphabet.last() && position == 0) || (tmp[0] != alphabet.last() && position == tmp.length-1)){ //blank space checker
				
				//checks fron blank space
				if(tmp[0] != alphabet.last() && position == 0){
					tmp = alphabet.last() + tmp
					position = position.inc()
					
					loop@ for (i in alphabet.indices){ //don't use break loops cause major points get taken off
						if (tmp[position] == alphabet[i]){
							tmp = tmp.substring(0, position) + alphabetChange[state][i] + tmp.substring(position + 1)
							state = commReference[state][i]
							break@loop
						}
					}
				}
				
				//checks back blank space
				else{
					tmp = tmp +  alphabet.last()
				}
			}
			//increments execution
			exec = exec.inc()
			
			//assigns new todo for next iteration
			toDo = action[state]
			
		}while(flag == 1)
		println("")
	}
	
}


//prints the necessary things for printing
fun printcommand(exec: Int, state: Int, current: Int, tape: String, script: String?){
	print("$exec: state=$state  ")
	
	for (i in tape.indices){
		if (i == current)
			print("[" + tape[i] + "] ")
		else
			print("" + tape[i] + " ")
	}
	
	println("   $script")
	
}
