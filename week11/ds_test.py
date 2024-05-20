#!/usr/bin/env python3
import json
from argparse import ArgumentParser
from pathlib import Path

from mark_client import print_results, parse_client_results
from ref_results import parse_ref_results

metrics = ["Turnaround time", "Resource utilisation", "Total rental cost"]
objectives = ["tt", "co"]

parser = ArgumentParser(description="Test marking script, e.g. ./ds_test.py \"java MyClient\" -n")
parser.add_argument("client_command_string", help="a command string used to run the client")
parser.add_argument("-p", "--port", required=True, type=int, help="port number")
parser.add_argument("-o", "--objective", choices=objectives, default="tt",
                    help="optimised objective (turnaround time, total rental cost)")
parser.add_argument("-i", "--innovation", action="store_true", help="mark a custom algorithm")
parser.add_argument("-c", "--config_dir", default="TestConfigs", help="config directory")
parser.add_argument("-n", "--newline", action="store_true", help="messages to ds-server are newline-terminated")
parser.add_argument("-r", "--reference_results", default="./results/ref_results.json", help="reference results path")
parser.add_argument("-t", "--client_results", help="client results path")
parser.add_argument("--process_reference_client", action="store_true",
                    help="run reference client instead of reading results file")
args = parser.parse_args()

if args.process_reference_client:
    reference_results = parse_ref_results(args.config_dir, metrics)
    out_path = Path(args.reference_results)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with open(out_path, 'w') as f:
        json.dump(reference_results, f, indent=2)
else:
    with open(args.reference_results, 'r') as f:
        reference_results = json.load(f)

if args.client_results:
    with open(args.client_results, 'r') as f:
        client_results = json.load(f)
else:
    client_results = parse_client_results(
        args.config_dir, metrics, args.client_command_string, args.newline, args.port, args.innovation)

print_results(client_results, reference_results, metrics, args.objective, args.innovation)