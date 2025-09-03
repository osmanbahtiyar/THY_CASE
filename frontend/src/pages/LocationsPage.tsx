import {
  AlertDialog,
  AlertDialogBody,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogOverlay,
  Box,
  Button,
  Container,
  Heading,
  HStack,
  IconButton,
  Input,
  FormControl,
  FormLabel,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  ModalFooter,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  VStack,
  useDisclosure,
  useToast,
  Select,
} from "@chakra-ui/react";
import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, Trash2, Pencil } from "lucide-react";

import { LocationApi } from "../api";
import type { Location } from "../api";

export default function LocationsPage({
  showError,
}: {
  showError?: (e: string | { title?: string; description?: string; violations?: string[] }) => void;
}) {
  const [locations, setLocations] = useState<Location[]>([]);
  const [pageNum, setPageNum] = useState(0);     
  const [pageSize] = useState(10);
  const [sortBy, setSortBy] = useState<string>("id");     
  const [direction, setDirection] = useState<"asc" | "desc">("asc");
  const [hasNext, setHasNext] = useState(false);

  const { isOpen, onOpen, onClose } = useDisclosure();
  const { isOpen: isSuccessOpen, onOpen: onSuccessOpen, onClose: onSuccessClose } = useDisclosure();
  const { isOpen: isDeleteOpen, onOpen: onDeleteOpen, onClose: onDeleteClose } = useDisclosure();
  const navigate = useNavigate();
  const toast = useToast();

  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [city, setCity] = useState("");
  const [country, setCountry] = useState("");

  const [editLocation, setEditLocation] = useState<Location | null>(null);
  const { isOpen: isEditOpen, onOpen: onEditOpen, onClose: onEditClose } = useDisclosure();
  const { isOpen: isUpdateSuccessOpen, onOpen: onUpdateSuccessOpen, onClose: onUpdateSuccessClose } = useDisclosure();

  const [toDeleteId, setToDeleteId] = useState<number | null>(null);
  const { isOpen: isConfirmOpen, onOpen: onConfirmOpen, onClose: onConfirmClose } = useDisclosure();
  const cancelRef = useRef<HTMLButtonElement | null>(null);
  const { isOpen: isUpdateConfirmOpen, onOpen: onUpdateConfirmOpen, onClose: onUpdateConfirmClose } = useDisclosure();
  const updateCancelRef = useRef<HTMLButtonElement | null>(null);

  const resetForm = () => {
    setCode("");
    setName("");
    setCity("");
    setCountry("");
  };

  const openEdit = (loc: Location) => {
    setEditLocation({ ...loc });
    onEditOpen();
  };

  const handleUpdate = async () => {
    if (!editLocation) return;

    if (
      !((editLocation as any).locationCode ?? (editLocation as any).code)?.trim() ||
      !editLocation.name?.trim() ||
      !editLocation.city?.trim() ||
      !editLocation.country?.trim()
    ) {
      toast({ status: "warning", title: "Please fill all fields" });
      return;
    }

    try {
      const payload: any = {
        name: editLocation.name.trim(),
        city: editLocation.city.trim(),
        country: editLocation.country.trim(),
        locationCode: ((editLocation as any).locationCode ?? (editLocation as any).code)
          .toString()
          .trim()
          .toUpperCase(),
      };

      await LocationApi.update(editLocation.id, payload);

      const page: any = await LocationApi.getAll(pageNum, pageSize, sortBy, direction);
      const items = (page as any).locations ?? (page as any).content ?? [];
      setLocations(items);
      setHasNext(!!(page as any).hasNext);

      onEditClose();
      setEditLocation(null);
      onUpdateSuccessOpen();
    } catch (e: any) {
      const detail =
        e?.response?.data?.detail ||
        e?.response?.data?.message ||
        e?.message ||
        "Update failed";

      const violations = Array.isArray(e?.response?.data?.violations)
        ? e.response.data.violations.map((v: any) =>
          v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
        )
        : undefined;
      showError?.({ title: "Update failed", description: detail, violations });
    }
  };

  useEffect(() => {
    console.log("Fetching locations with params", { pageNum, pageSize, sortBy, direction });
    LocationApi.getAll(pageNum, pageSize, sortBy, direction)
      .then((page: any) => {
        const items = page.locations ?? page.content ?? [];
        setLocations(items);
        setHasNext(!!page.hasNext);
      })
      .catch((e: any) => {
        const detail =
          e?.response?.data?.detail ||
          e?.response?.data?.message ||
          e?.message ||
          "Load failed";

        const violations = Array.isArray(e?.response?.data?.violations)
          ? e.response.data.violations.map((v: any) =>
            v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
          )
          : undefined;

        showError?.({ title: "Load failed", description: detail, violations });
      });
  }, [pageNum, pageSize, sortBy, direction]);

  const handleAdd = async () => {
    if (!code || !name || !city || !country) {
      toast({ status: "warning", title: "Please fill all fields" });
      return;
    }
    try {
      await LocationApi.getAll(pageNum, pageSize, sortBy, direction).then((page: any) => {
        const items = (page as any).locations ?? (page as any).content ?? [];
        setLocations(items);
        setHasNext(!!(page as any).hasNext);
      });
      onClose();       
      resetForm();     
      onSuccessOpen(); 
    } catch (e: any) {
      const detail =
        e?.response?.data?.detail ||
        e?.response?.data?.message ||
        e?.message ||
        "Create failed";

      const violations = Array.isArray(e?.response?.data?.violations)
        ? e.response.data.violations.map((v: any) =>
          v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
        )
        : undefined;
      showError?.({ title: "Create failed", description: detail, violations });
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await LocationApi.remove(id);
      const page: any = await LocationApi.getAll(pageNum, pageSize, sortBy, direction);
      const items = (page as any).locations ?? (page as any).content ?? [];
      setLocations(items);
      setHasNext(!!(page as any).hasNext);
      onDeleteOpen(); 
    } catch (e: any) {
      const detail =
        e?.response?.data?.detail ||
        e?.response?.data?.message ||
        e?.message ||
        "Delete failed";

      const violations = Array.isArray(e?.response?.data?.violations)
        ? e.response.data.violations.map((v: any) =>
          v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
        )
        : undefined;

      showError?.({ title: "Delete failed", description: detail, violations });
    }
  };

  return (
    <Container maxW="6xl" py={8}>
      <HStack justify="space-between" mb={4} wrap="wrap" spacing={3}>
        <Heading size="md">Locations</Heading>

        <HStack>
          <Select
            value={sortBy}
            onChange={(e) => {
              setPageNum(0);             
              setSortBy(e.target.value);
            }}
            size="sm"
            width="160px"
            bg="white"
          >
            <option value="id">ID</option>
            <option value="name">Name</option>
            <option value="city">City</option>
            <option value="country">Country</option>
            <option value="code">Code</option>
          </Select>

          <Select
            value={direction}
            onChange={(e) => {
              setPageNum(0);
              setDirection(e.target.value as "asc" | "desc");
            }}
            size="sm"
            width="120px"
            bg="white"
          >
            <option value="asc">ASC</option>
            <option value="desc">DESC</option>
          </Select>

          <Button leftIcon={<Plus size={16} />} onClick={onOpen}>
            Add Location
          </Button>
        </HStack>
      </HStack>

      <Box border="1px solid" borderColor="gray.200" borderRadius="md" overflowX="auto">
        <Table size="md">
          <Thead bg="gray.50">
            <Tr>
              <Th width="90px">ID</Th>
              <Th width="120px">Code</Th>
              <Th>Name</Th>
              <Th>City</Th>
              <Th>Country</Th>
              <Th width="80px" isNumeric>
                Actions
              </Th>
            </Tr>
          </Thead>
          <Tbody>
            {locations.map((l) => (
              <Tr key={l.id}>
                <Td>{l.id}</Td>
                <Td fontWeight="bold">{(l as any).code ?? (l as any).locationCode}</Td>
                <Td>{l.name}</Td>
                <Td>{l.city}</Td>
                <Td>{l.country}</Td>
                <Td isNumeric>
                  <HStack justify="flex-end">
                    <IconButton
                      aria-label="edit"
                      size="sm"
                      variant="outline"
                      onClick={() => openEdit(l as any)}
                      icon={<Pencil size={16} />}
                    />
                    <IconButton
                      aria-label="delete"
                      size="sm"
                      variant="outline"
                      onClick={() => {
                        setToDeleteId(l.id);
                        onConfirmOpen();
                      }}
                      icon={<Trash2 size={16} />}
                    />
                  </HStack>

                </Td>
              </Tr>
            ))}
          </Tbody>
        </Table>
      </Box>

      <HStack mt={4} justify="space-between">
        <Button
          onClick={() => setPageNum((p) => Math.max(p - 1, 0))}
          isDisabled={pageNum === 0}
          variant="outline"
        >
          Previous
        </Button>
        <Box>Page {pageNum + 1}</Box>
        <Button
          onClick={() => setPageNum((p) => p + 1)}
          isDisabled={!hasNext}
          variant="outline"
        >
          Next
        </Button>
      </HStack>

      {}
      <Modal
        isOpen={isOpen}
        onClose={() => {
          onClose();
          resetForm();
        }}
      >
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Add Location</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack align="stretch" spacing={3}>
              <FormControl isRequired>
                <FormLabel>Code</FormLabel>
                <Input
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  placeholder="e.g. IST"
                  maxLength={8}
                />
              </FormControl>
              <FormControl isRequired>
                <FormLabel>Name</FormLabel>
                <Input
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Airport name"
                />
              </FormControl>
              <FormControl isRequired>
                <FormLabel>City</FormLabel>
                <Input
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="City"
                />
              </FormControl>
              <FormControl isRequired>
                <FormLabel>Country</FormLabel>
                <Input
                  value={country}
                  onChange={(e) => setCountry(e.target.value)}
                  placeholder="Country"
                />
              </FormControl>
            </VStack>
          </ModalBody>
          <ModalFooter gap={2}>
            <Button
              variant="ghost"
              onClick={() => {
                onClose();
                resetForm();
              }}
            >
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={handleAdd}>
              Save
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {}
      <Modal
        isOpen={isEditOpen}
        onClose={() => {
          onEditClose();
          setEditLocation(null);
        }}
      >
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Edit Location</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <VStack align="stretch" spacing={3}>
              <FormControl isRequired>
                <FormLabel>Code</FormLabel>
                <Input
                  value={
                    (editLocation as any)?.locationCode ??
                    (editLocation as any)?.code ??
                    ""
                  }
                  onChange={(e) =>
                    setEditLocation((prev) =>
                      prev ? { ...prev, locationCode: e.target.value } : prev
                    )
                  }
                  placeholder="e.g. IST"
                  maxLength={8}
                />
              </FormControl>
              <FormControl isRequired>
                <FormLabel>Name</FormLabel>
                <Input
                  value={editLocation?.name ?? ""}
                  onChange={(e) =>
                    setEditLocation((prev) =>
                      prev ? { ...prev, name: e.target.value } : prev
                    )
                  }
                  placeholder="Airport name"
                />
              </FormControl>
              <FormControl isRequired>
                <FormLabel>City</FormLabel>
                <Input
                  value={editLocation?.city ?? ""}
                  onChange={(e) =>
                    setEditLocation((prev) =>
                      prev ? { ...prev, city: e.target.value } : prev
                    )
                  }
                  placeholder="City"
                />
              </FormControl>
              <FormControl isRequired>
                <FormLabel>Country</FormLabel>
                <Input
                  value={editLocation?.country ?? ""}
                  onChange={(e) =>
                    setEditLocation((prev) =>
                      prev ? { ...prev, country: e.target.value } : prev
                    )
                  }
                  placeholder="Country"
                />
              </FormControl>
            </VStack>
          </ModalBody>
          <ModalFooter gap={2}>
            <Button
              variant="ghost"
              onClick={() => {
                onEditClose();
                setEditLocation(null);
              }}
            >
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={onUpdateConfirmOpen}>
              Save
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {}
      <AlertDialog
        isOpen={isUpdateConfirmOpen}
        leastDestructiveRef={updateCancelRef}
        onClose={onUpdateConfirmClose}
        isCentered
      >
        <AlertDialogOverlay />
        <AlertDialogContent>
          <AlertDialogHeader fontSize="lg" fontWeight="bold">
            Update Location
          </AlertDialogHeader>
          <AlertDialogBody>
            Are you sure you want to save these changes?
          </AlertDialogBody>
          <AlertDialogFooter>
            <Button ref={updateCancelRef} onClick={onUpdateConfirmClose}>
              Cancel
            </Button>
            <Button
              colorScheme="blue"
              ml={3}
              onClick={async () => {
                await handleUpdate();
                onUpdateConfirmClose();
              }}
            >
              Confirm
            </Button>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {}
      <Modal isOpen={isSuccessOpen} onClose={onSuccessClose} isCentered>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Location Created</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            The location has been added successfully.
          </ModalBody>
          <ModalFooter>
            <Button
              colorScheme="blue"
              onClick={() => {
                onSuccessClose();
                navigate("/locations");
              }}
            >
              OK
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {}
      <Modal isOpen={isUpdateSuccessOpen} onClose={onUpdateSuccessClose} isCentered>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Location Updated</ModalHeader>
          <ModalCloseButton />
          <ModalBody>The location has been updated successfully.</ModalBody>
          <ModalFooter>
            <Button
              colorScheme="blue"
              onClick={() => {
                onUpdateSuccessClose();
                navigate("/locations");
              }}
            >
              OK
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {}
      <AlertDialog
        isOpen={isConfirmOpen}
        leastDestructiveRef={cancelRef}
        onClose={() => {
          onConfirmClose();
          setToDeleteId(null);
        }}
        isCentered
      >
        <AlertDialogOverlay />
        <AlertDialogContent>
          <AlertDialogHeader fontSize="lg" fontWeight="bold">
            Delete Location
          </AlertDialogHeader>
          <AlertDialogBody>
            Are you sure you want to delete this location? This action cannot be undone.
          </AlertDialogBody>
          <AlertDialogFooter>
            <Button
              ref={cancelRef}
              onClick={() => {
                onConfirmClose();
                setToDeleteId(null);
              }}
            >
              Cancel
            </Button>
            <Button
              colorScheme="red"
              ml={3}
              onClick={async () => {
                if (toDeleteId == null) return;
                await handleDelete(toDeleteId);
                onConfirmClose();
                setToDeleteId(null);
              }}
            >
              Delete
            </Button>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {}
      <Modal isOpen={isDeleteOpen} onClose={onDeleteClose} isCentered>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Location Deleted</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            The location has been deleted successfully.
          </ModalBody>
          <ModalFooter>
            <Button
              colorScheme="blue"
              onClick={() => {
                onDeleteClose();
                navigate("/locations");
              }}
            >
              OK
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Container>
  );
}